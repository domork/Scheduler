package domork.MySchedule.service;

import domork.MySchedule.endpoint.dto.GroupCredentialsDto;
import domork.MySchedule.endpoint.dto.GroupDto;
import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import org.springframework.web.bind.annotation.RequestBody;
import domork.MySchedule.exception.*;
import java.util.List;

public interface GroupService {
    /**
     * The new group is to be added.
     * @param group
     * The ID (positive Long number) will be generated in DB.
     *  name: unique name of the group.
     *  password: password to enter the group.
     *  time_to_start: time for participants to start.
     * Can be changed by adding time intervals.
     *  description: description of the group
     * @return same group with generated ID
     * @throws ValidationException when the group doesn't
     * fill the requirements (check them in the Validator).
     */
    Group createNewGroup (Group group);

    /**
     * Current user joins the group. The group updates
     * it's participants.
     * @param groupCredentials
     *   name: name of the group.
     *   password: password of the group.
     *   userID: The ID of user to join the group.
     * @return group if the credentials were correct.
     * @throws ValidationException when the groupCredentials don't
     * fill the requirements (check them in the Validator).
     */
    GroupMember joinGroupByNameAndPassword(GroupCredentials groupCredentials);

    /**
     * Provides all groups for authenticated user, in which user
     * participates.
     * @param ID of user
     * @return all groups, where ID is presented.
     * @throws ValidationException when the ID doesn't
     * fill the requirements (check them in the Validator).
     * @throws NotFoundException when no such ID is in DB.
     */
    List<Group> getGroupsByID(Long ID);

    /**
     * Gives back the group with the given name.
     * @param name of the group to provide.
     * @return group with the given name.
     * @throws ValidationException when name doesn't
     * fill the requirements (check them in the Validator).
     * @throws NotFoundException when no such group name is saved.
     */
    Group getGroupByName(String name);

    /**
     * Adds a new person to the group.
     * @param groupMember:
     *  group_id: ID of group, that will contain this user.
     *  user_id: ID of user, that will be added to the group.
     *  group_user_UUID: is ignored.
     * @return a new entity with the unique group-member-ID
     * @throws ValidationException when the ID does not exist
     * or current paar already exists.
     * @throws NotFoundException when either group or user is
     * not saved in DB.
     */
    GroupMember addMemberToTheGroup (GroupMember groupMember);

    /**
     * Adds the person's role to the group.
     * @param UUID is the uniques group-person-identifier
     * @param role is to be added role. Can be either
     *             'user','moderator' or 'admin'.
     * @return true, if the role was successfully added.
     * @throws ValidationException when a UUID already exists,
     * the UUID or role are not correct.
     * @throws NotFoundException when UUID is not presented in DB.
     */
    boolean addMemberRoleToTheGroup (String UUID, String role);
}
