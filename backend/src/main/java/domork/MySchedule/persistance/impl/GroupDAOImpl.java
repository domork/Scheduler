package domork.MySchedule.persistance.impl;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;
import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.exception.PersistenceException;
import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.persistance.GroupDAO;
import domork.MySchedule.security.services.UserPrinciple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

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
        try {
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
                    this.getUserPrinciple().getId(),
                    null, randomHexColor(), getUserPrinciple().getUsername())).getGroup_user_UUID();

            group.setUserUUID(UUID);
            addMemberRoleToTheGroup(UUID, "admin");
            group.setPassword(null);
            return group;
        } catch (DataAccessException e) {
            LOGGER.error("A group ({}) could not be added.", group, e);
            throw new PersistenceException("A group (" + group + ") could not be added.");
        }
    }

    private String randomHexColor() {
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }

    @Override
    public GroupMember joinGroupByNameAndPassword(GroupCredentials groupCredentials) {
        LOGGER.trace("joinGroupByNameAndPassword({})", groupCredentials);
        try {
            final String sql = "SELECT * FROM schedule_group" +
                    " WHERE name = ? and password = ?";


            List<Group> groups = jdbcTemplate.query(sql, preparedStatement -> {
                preparedStatement.setString(1, groupCredentials.getName());
                preparedStatement.setString(2, groupCredentials.getPassword());
            }, this::mapRow);
            if (groups.isEmpty()) {
                throw new ValidationException("The credentials are wrong.");
            }
            long groupID = groups.get(0).getID();
            String color = randomHexColor();
            String UUID = this.addMemberToTheGroup(new GroupMember
                    (groupID, groupCredentials.getUserID(), null,
                            color, groupCredentials.getUsername())).getGroup_user_UUID();
            this.addMemberRoleToTheGroup(UUID, "user");

            return new GroupMember(groupID, groupCredentials.getUserID(), UUID, color, groupCredentials.getUsername());
        } catch (DataAccessException e) {
            LOGGER.error("A group's join via credentials ({}) could not be done.", groupCredentials, e);
            throw new PersistenceException("A group's join via credentials (" + groupCredentials + ") could not be done.");
        }
    }

    @Override
    public List<Group> getGroupsByID(Long ID) {
        LOGGER.trace("getGroupsByID({})", ID);

        try {
            final String sql = "SELECT s.id, s.name, s.time_to_start," +
                    " s.description, group_user_uuid FROM " + "group_members"
                    + " h RIGHT JOIN schedule_group s on h.group_id= s.id WHERE user_id= ? ORDER BY name";
            List<Group> groups = jdbcTemplate.query(sql, preparedStatement -> preparedStatement.setLong(1, ID), this::mapRow);
            if (groups.isEmpty())
                throw new NotFoundException("Groups with the ID " + ID + " were not found");

            return groups;
        } catch (DataAccessException e) {
            LOGGER.error("Could not get groups by ID {}", ID, e);
            throw new PersistenceException("Could not get groups by ID " + ID);
        }
    }

    @Override
    public void getGroupByName(String name) {
        LOGGER.trace("getGroupByName({})", name);

        try {
            final String sql = "SELECT * FROM " + TABLE_NAME
                    + " WHERE name= ?";

            List<Group> groups = jdbcTemplate.query(sql, preparedStatement -> preparedStatement.setString(1, name), this::mapRow);
            if (groups.isEmpty())
                throw new NotFoundException("Group with the name " + name + " was not found");
        } catch (DataAccessException e) {
            LOGGER.error("Could not get group by name {}", name, e);
            throw new PersistenceException("Could not get groups by ID " + name);
        }
    }

    @Override
    public GroupMember addMemberToTheGroup(GroupMember groupMember) {
        LOGGER.trace("addMemberToTheGroup({})", groupMember);
        try {
            groupMember.setGroup_user_UUID(UUID.randomUUID().toString());
            final String sql = "INSERT INTO group_members (group_id, user_id, group_user_UUID, color, name) VALUES (?,?,?,?,?);";
            jdbcTemplate.update(connection -> {
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                int paramIndex = 1;
                statement.setLong(paramIndex++, groupMember.getGroup_id());
                statement.setLong(paramIndex++, groupMember.getUser_id());
                statement.setString(paramIndex++, groupMember.getGroup_user_UUID());
                statement.setString(paramIndex++, groupMember.getColor());
                statement.setString(paramIndex, groupMember.getName());
                return statement;
            });

            return groupMember;
        } catch (DataAccessException e) {
            LOGGER.error("Could not add member ({}) to the group", groupMember, e);
            throw new PersistenceException("Could not add member (" + groupMember + ") to the group");
        }
    }

    @Override
    public boolean addMemberRoleToTheGroup(String UUID, String role) {
        LOGGER.trace("addMemberRoleToTheGroup({}, {})", role, UUID);
        try {
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
        } catch (DataAccessException e) {
            LOGGER.error("Could not add member ({}) role ({}) to the group", UUID, role, e);
            throw new PersistenceException("Could not add member (" + UUID + ") role (" + role + ") to the group");
        }
    }

    @Override
    public List<TimeIntervalByUser> getGroupInfoForSpecificDate(Long groupID, LocalDate date) {
        LOGGER.trace("getGroupInfoForSpecificDate({}, {})", groupID, date);
        try {
            String uuid = getUUIDOfCurrentUserByGroupId(groupID);
            final String sql =
                    "WITH q AS ( WITH default_group_users AS (SELECT DISTINCT g.group_user_uuid, g.color, g.name" +
                            " FROM group_members g" +
                            " LEFT JOIN time_of_unique_user_in_group t ON g.group_user_uuid=t.group_user_uuid" +
                            " WHERE g.group_id = ? " +
                            " ORDER BY g.name)" +
                            " SELECT d.group_user_uuid, time_start, time_end, color, name" +
                            " FROM default_group_users d" +
                            " full outer join (SELECT *" +
                            "                FROM time_of_unique_user_in_group" +
                            "        WHERE ? >= time_end" +
                            " AND time_end >= ?) t on t.group_user_uuid = d.group_user_uuid" +
                            " WHERE name IS NOT NULL " +
                            "ORDER BY time_start, name NULLS LAST) SELECT * FROM q WHERE group_user_uuid = ? " +
                            "UNION ALL SELECT * FROM q WHERE group_user_uuid != ?";

            return jdbcTemplate.query(sql, preparedStatement -> {
                        preparedStatement.setLong(1, groupID);
                        preparedStatement.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay().plusDays(1)));
                        preparedStatement.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay()));
                        preparedStatement.setString(4, uuid);
                        preparedStatement.setString(5, uuid);

                    },
                    this::mapRowTimeIntervalByUser);
        } catch (DataAccessException e) {
            LOGGER.error("Could not get group ({}) info for date ({})", groupID, date, e);
            throw new PersistenceException("Could not get group (" + groupID + ") info for date (" + date + ")");
        }
    }

    @Override
    public List<TimeIntervalByUser> getGroupInfoForSpecificDateWithFullIntervalsOnly(Long groupID, LocalDate date) {
        LOGGER.trace("getGroupInfoForSpecificDateWithIntervalsOnly({}, {})", groupID, date);
        try {
            final String sql = "SELECT g.group_user_uuid, time_start, time_end, g.color, g.name " +
                    "FROM group_members g " +
                    "natural join time_of_unique_user_in_group t  " +
                    "WHERE g.group_id =  ? " +
                    "AND ? >= time_end " +
                    "AND time_end >= ?" +
                    " ORDER BY g.name";


            return jdbcTemplate.query(sql, preparedStatement -> {
                        preparedStatement.setLong(1, groupID);
                        preparedStatement.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay().plusDays(1)));
                        preparedStatement.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay()));
                    },
                    this::mapRowTimeIntervalByUser);
        } catch (DataAccessException e) {
            LOGGER.error("Could not get non-null group ({}) info for date ({}) ", groupID, date, e);
            throw new PersistenceException("Could not get non-null group (" + groupID + ") info for date (" + date + ")");
        }
    }

    @Override
    public TimeIntervalByUser addNewInterval(TimeIntervalByUser timeIntervalByUser) {
        LOGGER.trace("addNewInterval({})", timeIntervalByUser);
        try {
            final String sql = "INSERT INTO time_of_unique_user_in_group " +
                    "(group_user_uuid, time_start,time_end) VALUES (?,?,?);";
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
        } catch (DataAccessException e) {
            LOGGER.error("Could not add a new interval ({}) ", timeIntervalByUser, e);
            throw new PersistenceException("Could not add a new interval " + timeIntervalByUser);
        }

    }

    @Override
    public List<TimeIntervalByUser> getMemberInfoForSpecificDate(String UUID, LocalDate date) {
        LOGGER.trace("getMemberInfoForSpecificDate({}, {})", UUID, date);
        try {
            final String sql = "SELECT * FROM time_of_unique_user_in_group" +
                    " WHERE group_user_uuid =? " +
                    "AND ? >= time_end " +
                    "AND time_end >= ? ORDER BY time_end";


            return jdbcTemplate.query(sql, preparedStatement -> {
                        preparedStatement.setString(1, UUID);
                        preparedStatement.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay().plusDays(1)));
                        preparedStatement.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay()));
                    },
                    this::mapRowTimeIntervalByUserSmall);
        } catch (DataAccessException e) {
            LOGGER.error("Could not add get a member ({}) info for a date {}", UUID, date, e);
            throw new PersistenceException("Could not add get a member (" + UUID + ") info for a date " + date);
        }
    }

    @Override
    public void deleteInterval(String UUID, Timestamp date) {
        LOGGER.trace("deleteInterval({}, {})", UUID, date);
        try {
            String sql = "DELETE FROM time_of_unique_user_in_group" +
                    " WHERE group_user_uuid=? ";
            LocalDateTime tempTime = date.toLocalDateTime();
            if (tempTime.getHour() == 0 && tempTime.getMinute() == 0) {
                sql += "AND ? > time_end AND time_end > ?";
                jdbcTemplate.update(sql, UUID, Timestamp.valueOf(date.toLocalDateTime().plusDays(1)), date);

            } else {
                sql += "AND time_end = ? ";
                jdbcTemplate.update(sql, UUID, date);
            }
        } catch (DataAccessException e) {
            LOGGER.error("Could not delete interval of user {} by this date: {}", UUID, date, e);
            throw new PersistenceException("Could not delete interval of user {" + UUID + "} by this date: " + date);
        }

    }

    @Override
    public void leaveGroup(Long groupID) {
        Long userID = this.getUserPrinciple().getId();
        LOGGER.trace("leaveGroup with ID {}", groupID);

        try {
            String sql = "DELETE FROM time_of_unique_user_in_group WHERE group_user_uuid = " +
                    "(SELECT group_user_uuid FROM group_members WHERE group_id=? AND user_id=?);" +
                    "DELETE FROM role_of_user_in_specific_group WHERE " +
                    "group_user_uuid =(SELECT group_user_uuid FROM group_members WHERE group_id=? AND user_id=?); " +
                    "DELETE FROM group_members WHERE  group_id = ? AND user_id = ? ";
            jdbcTemplate.update(sql, groupID, userID, groupID, userID, groupID, userID);
        } catch (DataAccessException e) {
            LOGGER.error("User with ID {} could not leave group with ID {}", userID, groupID, e);
            throw new PersistenceException("User with ID {" + userID + "} could not leave group with ID " + groupID);
        }
    }

    @Override
    public String getUUIDOfCurrentUserByGroupId(Long groupID) {
        LOGGER.trace("getUUIDOfCurrentUserByGroupId with ID {}", groupID);
       try {
            String sql = "SELECT group_user_uuid FROM group_members WHERE group_id = ?" +
                    " AND user_id = ?";
            List<String> list = jdbcTemplate.query(sql, preparedStatement -> {
                        preparedStatement.setLong(1, groupID);
                        preparedStatement.setLong(2, this.getUserPrinciple().getId());
                    }
                    , this::getUUIDFromMapRow);
            if (list.isEmpty())
                throw new NotFoundException("nope");
            return list.get(0);
        }
       catch (DataAccessException e) {
           LOGGER.error("Could not get UUID of group by it's id {}", groupID, e);
           throw new PersistenceException("Could not get UUID of group by it's id "+ groupID);
       }
    }

    @Override
    public void calculateNextMeetingByGroupId(Long groupID) {
        LOGGER.trace("calculateNextMeetingByGroupId with ID {}", groupID);

        try {
            List<TimeIntervalByUser> temp;
            long[] hours = new long[23];
            long[] minutes = new long[23];
            LocalDateTime localDateTime = null;
            int hourStart;
            int hourEnd;
            int minuteStart;
            int minuteEnd;
            for (int i = 0; i < 7; i++) {
                temp = this.getGroupInfoForSpecificDateWithFullIntervalsOnly(groupID, (LocalDate.now().plusDays(i)));
                if (!temp.isEmpty()) {
                    for (TimeIntervalByUser t : temp) {
                        localDateTime = t.getTime_end().toLocalDateTime();
                        hourEnd = localDateTime.getHour();
                        minuteEnd = localDateTime.getMinute();
                        localDateTime = t.getTime_start().toLocalDateTime();
                        hourStart = localDateTime.getHour();
                        minuteStart = localDateTime.getMinute();
                        for (int j = 0; j <= hourEnd - hourStart; j++) {
                            hours[hourEnd - j]++;
                            minutes[hourEnd - j]++;
                        }
                        if (minuteEnd < 45) {
                            minutes[hourEnd]--;
                            if (minuteEnd < 15) {
                                hours[hourEnd]--;
                            }
                        }
                        if (minuteStart > 15) {
                            hours[hourStart]--;
                            if (minuteStart > 45) {
                                minutes[hourStart]--;
                            }
                        }
                    }
                    long max = 0;
                    int pos = 0;
                    boolean isHours = true;
                    for (int j = 0; j < hours.length; j++) {
                        if (hours[j] > max) {
                            max = hours[j];
                            isHours = true;
                            pos = j;
                        }
                        if (minutes[j] > max) {
                            max = minutes[j];
                            isHours = false;
                            pos = j;
                        }
                    }
                    Timestamp time = Timestamp.valueOf(localDateTime.withHour(pos).withMinute(isHours ? 0 : 30));
                    String sql = "UPDATE schedule_group SET time_to_start = ? WHERE id =?";
                    jdbcTemplate.update(sql, time, groupID);
                    return ;
                }
            }
            String sql = "UPDATE schedule_group SET time_to_start = NULL WHERE id =?";
            jdbcTemplate.update(sql, groupID);
        }
        catch (DataAccessException e) {
            LOGGER.error("Could not calculate next meeting by group ID {}", groupID, e);
            throw new PersistenceException("Could not calculate next meeting by group ID {}"+ groupID);
        }
    }

    @Override
    public GroupMember getGroupMemberInfoByUUID(String UUID) {
        LOGGER.trace("getGroupMemberInfoByGroupID with ID {}", UUID);
       try {
            String sql = "SELECT * FROM group_members WHERE group_user_uuid=?";
            List<GroupMember> groupMember = jdbcTemplate.query
                    (sql, preparedStatement -> preparedStatement.setString(1, UUID), this::groupMemberMapRow);
            if (groupMember.isEmpty())
                throw new NotFoundException("You are either not part of this group or this group doesn't exist.");
            return groupMember.get(0);
        }
       catch (DataAccessException e) {
           LOGGER.error("Could not get group member info by UUID  {}", UUID, e);
           throw new PersistenceException("Could not get group member info by UUID " + UUID);
       }
    }

    @Override
    public void updateGroupMemberInfoByUUID(String UUID, String color, String name) {
        LOGGER.trace("updateGroupMemberInfoByUUID with ID {}. New color is: {}, and new name is: {}", UUID, color, name);
        try {
            String sql = "UPDATE group_members SET color = ?, name=? WHERE group_user_uuid =?";
            jdbcTemplate.update(sql, color, name, UUID);
        }
        catch (DataAccessException e) {
            LOGGER.error("Could not update group member info by UUID {} with color {} and name {}", UUID,color,name, e);
            throw new PersistenceException
                    ("Could not update group member info by UUID "+UUID+" with color "+color+" and name  " +name );
        }
    }

    private String getUUIDFromMapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getString("group_user_uuid");
    }

    private Group mapRow(ResultSet resultSet, int i) throws SQLException {
        final Group group = new Group();
        group.setID(resultSet.getLong("id"));

        group.setName(resultSet.getString("name"));
        group.setDescription(resultSet.getString("description"));
        group.setTime_to_start(resultSet.getTimestamp("time_to_start"));
        String userUUID = null;
        try {
            userUUID = resultSet.getString("group_user_uuid");
        } catch (SQLException ignored) {
        }
        group.setUserUUID(userUUID);
        return group;
    }

    private TimeIntervalByUser mapRowTimeIntervalByUser(ResultSet resultSet, int i) throws SQLException {
        final TimeIntervalByUser timeIntervalByUser = new TimeIntervalByUser();
        timeIntervalByUser.setColor(resultSet.getString("color"));
        try {
            timeIntervalByUser.setTime_end(resultSet.getTimestamp("time_end"));
            timeIntervalByUser.setTime_start(resultSet.getTimestamp("time_start"));
        } catch (SQLException ignored) {
        }
        timeIntervalByUser.setGroup_user_UUID(resultSet.getString("group_user_uuid"));
        timeIntervalByUser.setName(resultSet.getString("name"));
        return timeIntervalByUser;
    }

    private TimeIntervalByUser mapRowTimeIntervalByUserSmall(ResultSet resultSet, int i) throws SQLException {
        final TimeIntervalByUser timeIntervalByUser = new TimeIntervalByUser();
        timeIntervalByUser.setTime_end(resultSet.getTimestamp("time_end"));
        timeIntervalByUser.setTime_start(resultSet.getTimestamp("time_start"));
        timeIntervalByUser.setGroup_user_UUID(resultSet.getString("group_user_uuid"));
        return timeIntervalByUser;
    }

    private UserPrinciple getUserPrinciple() {
        return ((UserPrinciple) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal());
    }

    private GroupMember groupMemberMapRow(ResultSet resultSet, int i) throws SQLException {
        final GroupMember groupMember = new GroupMember();
        groupMember.setGroup_id(resultSet.getLong("group_id"));
        groupMember.setUser_id(resultSet.getLong("user_id"));
        groupMember.setColor(resultSet.getString("color"));
        groupMember.setName(resultSet.getString("name"));
        groupMember.setGroup_user_UUID(resultSet.getString("group_user_uuid"));
        return groupMember;
    }
}
