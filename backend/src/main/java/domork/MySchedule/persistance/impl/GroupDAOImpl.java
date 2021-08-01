package domork.MySchedule.persistance.impl;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
                        getAuthentication().getPrincipal()).getId(), null)).getGroup_user_UUID();
        addMemberRoleToTheGroup(UUID, "admin");

        return group;
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
        String UUID = this.addMemberToTheGroup(new GroupMember
                (groupID, groupCredentials.getUserID(), null)).getGroup_user_UUID();
        this.addMemberRoleToTheGroup(UUID, "user");

        return new GroupMember(groupID, groupCredentials.getUserID(), UUID);
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
        final String sql = "INSERT INTO group_members (group_id, user_id, group_user_UUID) VALUES (?,?,?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            statement.setLong(paramIndex++, groupMember.getGroup_id());
            statement.setLong(paramIndex++, groupMember.getUser_id());
            statement.setString(paramIndex, groupMember.getGroup_user_UUID());
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

}
