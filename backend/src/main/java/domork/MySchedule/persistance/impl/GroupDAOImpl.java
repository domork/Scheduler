package domork.MySchedule.persistance.impl;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;
import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.persistance.GroupDAO;
import domork.MySchedule.security.services.UserPrinciple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Date;

@Service
public class GroupDAOImpl implements GroupDAO {
    private static final String TABLE_NAME = "schedule_group";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final JdbcTemplate jdbcTemplate;

    public GroupDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Group createNewGroup(Group group) {
        LOGGER.trace("createNewGroup({})", group);
        final String sql = "INSERT INTO schedule_group " +
                "(name, password, description, time_to_start) VALUES (?,?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            statement.setString(paramIndex++, group.getName());
            statement.setString(paramIndex++, group.getPassword());
            statement.setString(paramIndex++, group.getDescription());
            statement.setTimestamp(paramIndex, group.getTime_to_start());
            return statement;
        }, keyHolder);
        group.setID(((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue());
        String UUID = addMemberToTheGroup(new GroupMember(group.getID(),
                ((UserPrinciple) SecurityContextHolder.getContext().
                        getAuthentication().getPrincipal()).getId(),
                null, randomHexColor())).getGroup_user_UUID();
        addMemberRoleToTheGroup(UUID, "admin");

        return group;
    }

    private String randomHexColor(){
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }

    @Override
    public GroupMember joinGroupByNameAndPassword(GroupCredentials groupCredentials) {
        LOGGER.trace("joinGroupByNameAndPassword({})", groupCredentials);
        final String sql = "SELECT * FROM schedule_group" +
                " WHERE name = '" + groupCredentials.getName() + "' and password = '" + groupCredentials.getPassword()+"'";
        List<Group> groups = jdbcTemplate.query(sql, this::mapRow);
        if (groups.isEmpty()) {
            throw new ValidationException("The credentials are wrong.");
        }
        long groupID = groups.get(0).getID();
        String color =  randomHexColor();
        String UUID = this.addMemberToTheGroup(new GroupMember
                (groupID, groupCredentials.getUserID(), null,
                      color )).getGroup_user_UUID();
        this.addMemberRoleToTheGroup(UUID, "user");

        return new GroupMember(groupID, groupCredentials.getUserID(), UUID,color);
    }

    @Override
    public List<Group> getGroupsByID(Long ID) {
        LOGGER.trace("getGroupsByID({})", ID);
        final String sql = "SELECT * FROM " + "group_members"
                + " h RIGHT JOIN schedule_group s on h.group_id= s.id WHERE user_id='" + ID + "'";
        List<Group> groups = jdbcTemplate.query(sql, this::mapRow);
        if (groups.isEmpty())
            throw new NotFoundException("Groups with the ID " + ID + " were not found");
        return groups;
    }

    @Override
    public Group getGroupByName(String name) {
        LOGGER.trace("getGroupByName({})", name);

        final String sql = "SELECT * FROM " + TABLE_NAME
                + " WHERE name='" + name + "'";

        List<Group> groups = jdbcTemplate.query(sql, this::mapRow);
        if (groups.isEmpty())
            throw new NotFoundException("Group with the name " + name + " was not found");
        return groups.get(0);
    }

    @Override
    public GroupMember addMemberToTheGroup(GroupMember groupMember) {
        LOGGER.trace("addMemberToTheGroup({})", groupMember);
        groupMember.setGroup_user_UUID(UUID.randomUUID().toString());
        final String sql = "INSERT INTO group_members (group_id, user_id, group_user_UUID, color) VALUES (?,?,?,?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            statement.setLong(paramIndex++, groupMember.getGroup_id());
            statement.setLong(paramIndex++, groupMember.getUser_id());
            statement.setString(paramIndex++, groupMember.getGroup_user_UUID());
            statement.setString(paramIndex, groupMember.getColor());
            return statement;
        });

        return groupMember;
    }

    @Override
    public boolean addMemberRoleToTheGroup(String UUID, String role) {
        LOGGER.trace("addMemberRoleToTheGroup({}, {})", role, UUID);
        final String sql = "INSERT INTO role_of_user_in_specific_group (group_user_uuid, role) VALUES (?,?::schedule_user_roles);";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            statement.setString(paramIndex++, UUID);
            statement.setString(paramIndex, role);
            return statement;
        });
        return true;
    }

    @Override
    public List<TimeIntervalByUser> getGroupInfoForSpecificDate(Long groupID, LocalDate date) {
        LOGGER.trace("getGroupInfoForSpecificDate({}, {})", groupID, date);
        System.out.println(date);
        final String sql = "SELECT a.group_user_uuid, time_start, time_end, a.color FROM " +
                "(schedule_group s natural join group_members g ) a " +
                "join time_of_unique_user_in_group t on t.group_user_uuid = a.group_user_uuid" +
                " WHERE a.id='" + groupID + "' AND '"+Timestamp.valueOf(date.atStartOfDay().plusDays(1)) + "'>= time_end AND time_end >= '"+Timestamp.valueOf(date.atStartOfDay()) + "'; ";
        return jdbcTemplate.query(sql, this::mapRowTimeIntervalByUser);
    }

    @Override
    public TimeIntervalByUser addNewInterval(TimeIntervalByUser timeIntervalByUser) {
        LOGGER.trace("addNewInterval({})", timeIntervalByUser);
        final String sql = "INSERT INTO time_of_unique_user_in_group (group_user_uuid, time_start,time_end) VALUES (?,?,?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS);
            int paramIndex = 1;
            statement.setString(paramIndex++, timeIntervalByUser.getGroup_user_UUID());
            statement.setTimestamp(paramIndex++, timeIntervalByUser.getTime_start());
            statement.setTimestamp(paramIndex, timeIntervalByUser.getTime_end());
            return statement;
        });
        return timeIntervalByUser;

    }

    private Group mapRow(ResultSet resultSet, int i) throws SQLException {
        final Group group = new Group();
        group.setID(resultSet.getLong("id"));
        group.setPassword(resultSet.getString("password"));
        group.setName(resultSet.getString("name"));
        group.setDescription(resultSet.getString("description"));
        group.setTime_to_start(resultSet.getTimestamp("time_to_start"));
        return group;
    }

    private GroupMember mapRowGroupMember(ResultSet resultSet, int i) throws SQLException {
        final GroupMember groupMember = new GroupMember();
        groupMember.setGroup_id(resultSet.getLong("group_ID"));
        groupMember.setUser_id(resultSet.getLong("user_ID"));
        groupMember.setGroup_user_UUID(resultSet.getString("group_user_UUID"));
        return groupMember;
    }

    private TimeIntervalByUser mapRowTimeIntervalByUser(ResultSet resultSet, int i) throws SQLException{
        final TimeIntervalByUser timeIntervalByUser = new TimeIntervalByUser();
        timeIntervalByUser.setColor(resultSet.getString("color"));
        timeIntervalByUser.setTime_end(resultSet.getTimestamp("time_end"));
        timeIntervalByUser.setTime_start(resultSet.getTimestamp("time_start"));
        timeIntervalByUser.setGroup_user_UUID(resultSet.getString("group_user_uuid"));
        return timeIntervalByUser;
    }

}
