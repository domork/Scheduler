package domork.MySchedule.persistance;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;
import domork.MySchedule.exception.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface GroupDAO {
    /**
     * The new group is to be added.
     * @param group
     *  The ID (positive Long number) will be generated in DB.
     *  name*: unique name of the group.
     *  password*: password to enter the group.
     *  time_to_start: time for participants to start.
     * Can be changed by adding time intervals.
     *  description: description of the group
     *  userUUID: unique global ID of the user in the group
     * @return same group with generated ID
     * if no exception is thrown.
     * @throws PersistenceException when something goes wrong with
     * the DB. (E.X. no connection).
     */
    Group createNewGroup (Group group);

    /**
     * Current user joins the group. The group updates
     * it's participants.
     * @param groupCredentials
     *   name*: name of the group.
     *   password*: password of the group.
     *   userID*: The ID of user to join the group.
     *   username*: representative name of user in the group.
     * @return group if the credentials were correct.
     * @throws PersistenceException when something goes wrong with
     * the DB. (E.X. no connection).
     */
    GroupMember joinGroupByNameAndPassword(GroupCredentials groupCredentials);

    /**
     * Provides all groups for authenticated user, in which user
     * participates.
     * @param ID of user
     * @return all groups, where ID is presented.
     * @throws PersistenceException when something goes wrong with
     * the DB. (e.x. no connection).
     */
    List<Group> getGroupsByID(Long ID);

    /**
     * Gives back the group with the given name.
     * @param name of the group to provide.
     * @return group with the given name.
     * @throws NotFoundException when no group
     * with given name is stored.
     */
    Group getGroupByName(String name);

    /**
     * Adds a new person to the group.
     * @param groupMember*:
     *  group_id: ID of group, that will contain this user.
     *  user_id: ID of user, that will be added to the group.
     *  group_user_UUID: is ignored.
     *  color: in which color (HEX) should be the graph printed.
     *  name: representative name of user in the group.
     * @return a new entity with the unique group-member-ID
     * @throws PersistenceException when something goes wrong with
     * the DB. (e.x. no connection).
     */
    GroupMember addMemberToTheGroup (GroupMember groupMember);

    /**
     * Adds the person's role to the group.
     * @param UUID is the uniques group-person-identifier
     * @param role is to be added role. Can be either
     *             'user','moderator' or 'admin'.
     * @return true, if the role was successfully added.
     * @throws PersistenceException when something goes wrong with
     * the DB. (e.x. no connection).
     */
    boolean addMemberRoleToTheGroup (String UUID, String role);

    /**
     * Provides info about all group's members intervals at given date.
     * Contains all members, even if they have no intervals at this day.
     * @param groupID of group to be fetched.
     * @param date will be set to the 00:00 of given day,
     *             such that the whole day could be fetched.
     * @return the list of
     *    group_user_UUID: unique global user ID in the group.
     *    time_start: user's start of time interval.
     *    time_end: user's start of time interval.
     *    color: in which color (HEX) should be the graph printed.
     *    name: representative name of user in the group.
     */
    List <TimeIntervalByUser> getGroupInfoForSpecificDate (Long groupID, LocalDate date);

    /**
     * Same as getGroupInfoForSpecificDate but returns non-empty intervals.
     * @param groupID of group to be fetched.
     * @param date will be set to the 00:00 of given day,
     *             such that the whole day could be fetched.
     * @return the list of
     *    group_user_UUID: unique global user ID in the group.
     *    time_start: user's start of time interval.
     *    time_end: user's start of time interval.
     *    color: in which color (HEX) should be the graph printed.
     *    name: representative name of user in the group.
     */
    List <TimeIntervalByUser>
    getGroupInfoForSpecificDateWithFullIntervalsOnly(Long groupID, LocalDate date);

    /**
     * Adds a new Interval by a user.
     * @param timeIntervalByUser
     *        group_user_UUID: unique global user ID in the group.
     *        time_start: user's start of time interval.
     *        time_end: user's start of time interval.
     *        color: in which color (HEX) should be the graph printed.
     *        name: representative name of user in the group.
     * @return same interval, if it was successfully added.
     */
    TimeIntervalByUser addNewInterval (TimeIntervalByUser timeIntervalByUser);

    /**
     * Works almost same as getGroupInfoForSpecificDate. Instead, only one
     * member's intervals are searched (not the whole group's one).
     * @param UUID of user in specific group to check.
     * @param date the day, in which intervals will be returned.
     * @return the list of all intervals by a given user at specific date (date's day).
     */
    List <TimeIntervalByUser> getMemberInfoForSpecificDate (String UUID, LocalDate date);

    /**
     * Removes the interval from a specific user in the group.
     * Only the admin and the owner itself can delete the interval
     * @param UUID of the user in the group
     * @param date the end of the interval. If the time is of 00:00,
     *             then it will delete all intervals in that day.
     */
    void deleteInterval(String UUID, Timestamp date);

    /**
     * Leaves the group and deletes all intervals from that user.
     * @param groupID of group to be exited from/
     */
    void leaveGroup(Long groupID);

    /**
     * Provides the UUID of the current logged-in user, for a given group id.
     * When there is no such a relation of user and group -> null is returned.
     * @param groupID of group
     * @return unique UUID of group's member.
     */
    String getUUIDOfCurrentUserByGroupId(Long groupID);

    /**
     * Computes the time, that would work for most of the people
     * for this/next 6 days.
     * If no one has interval for the day, then it will calculate the next day.
     * @param groupID of group, which time is needed.
     * @return the time of next meeting.
     * If the time is epoch => no interval for 7 days is given.
     */
    Timestamp calculateNextMeetingByGroupId(Long groupID);
}
