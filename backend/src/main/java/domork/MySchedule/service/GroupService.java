package domork.MySchedule.service;

import domork.MySchedule.endpoint.dto.GroupCredentialsDto;
import domork.MySchedule.endpoint.dto.GroupDto;
import domork.MySchedule.endpoint.dto.TimeIntervalByUserDto;
import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;
import org.springframework.web.bind.annotation.RequestBody;
import domork.MySchedule.exception.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface GroupService {
    /*
        If parameter has a * -> required.
     */

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
     *            will be generated in DB
     * @return same group with generated ID
     * @throws ValidationException when the group doesn't
     * fill the requirements (check them in the Validator.groupCheck()).
     * @throws ValidationException when the group with
     * same name already exists.
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
     * @throws ValidationException when the groupCredentials doesn't
     * fill the requirements
     * (check them in the Validator.groupCredentialsCheck()).
     */
    GroupMember joinGroupByNameAndPassword(GroupCredentials groupCredentials);

    /**
     * Provides all groups for authenticated user, in which user
     * participates.
     * @param ID* of user
     * @return all groups, where ID is presented.
     * @throws ValidationException when the ID doesn't
     * fill the requirements (check them in the Validator).
     * @throws NotFoundException when no such ID is in DB.
     */
    List<Group> getGroupsByID(Long ID);

    /**
     * Gives back the group with the given name.
     * @param name* of the group to provide.
     * @return group with the given name.
     * @throws ValidationException when name doesn't
     * fill the requirements (check them in the Validator).
     * @throws NotFoundException when no such group name is saved.
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
     * @throws ValidationException when the ID does not exist
     * or current paar already exists.
     *  @throws ValidationException when groupMember doesn't
     *  fill the requirements
     *  (check them in the Validator.groupMemberCheck()).
     * @throws NotFoundException when either group or user is
     * not saved in DB.
     */
    GroupMember addMemberToTheGroup (GroupMember groupMember);

    /**
     * Adds the person's role to the group.
     * @param UUID* is the uniques group-person-identifier
     * @param role is to be added role. Can be either
     *             'user','moderator' or 'admin'.
     * @return true, if the role was successfully added.
     * @throws ValidationException when a UUID already exists,
     * the UUID or role are not correct.
     * @throws NotFoundException when UUID is not presented in DB.
     */
    boolean addMemberRoleToTheGroup (String UUID, String role);

    /**
     * Provides info about all group's members intervals at given date.
     * @param groupID of group to be fetched.
     * @param date will be set to the 00:00 of given day,
     *             such that the whole day could be fetched.
     * @return the list of
     *    group_user_UUID: unique global user ID in the group.
     *    time_start: user's start of time interval.
     *    time_end: user's start of time interval.
     *    color: in which color (HEX) should be the graph printed.
     *    name: representative name of user in the group.
     *  @throws ValidationException when a groupID doesn't exist,
     *   or has a wrong format.
     *   ToDO: I assume that the date validation is not required since
     *    it is auto parsed in endpoint
     *    DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME);
     *   @throws ValidationException if current user does not
     *   participate in given groupID.
     */
    List <TimeIntervalByUser> getGroupInfoForSpecificDate (Long groupID, LocalDate date);


    /**
     * Adds a new Interval by a user.
     * @param timeIntervalByUser
     *        group_user_UUID: unique global user ID in the group.
     *        time_start: user's start of time interval.
     *        time_end: user's start of time interval.
     *        color: in which color (HEX) should be the graph printed.
     *        name: representative name of user in the group.
     * @param groupID for validation.
     * @return same interval, if it was successfully added.
     * @throws ValidationException when timeIntervalByUser doesn't
     *         fill the requirements
     *         (check them in the Validator.timeIntervalByUserCheck()).
     */
    TimeIntervalByUser addNewInterval (TimeIntervalByUser timeIntervalByUser, Long groupID);

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

    /**
     * Provides the member info of current user,
     * such as color or name by the given group ID.
     * @param UUID is a combo of the group and user
     * @return member info
     */
    GroupMember getGroupMemberInfoByUUID (String UUID);

    /**
     * Update the color and name of user by given UUID.
     * @param UUID is the unique group-user relationship.
     * @param color is the new value for change. Must be in hex format.
     * @param name is the new value for change.
     */
    void updateGroupMemberInfoByUUID (String UUID, String color, String name);
}
