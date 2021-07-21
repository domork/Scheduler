package domork.MySchedule.persistance;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.exception.*;

import java.util.List;

public interface GroupDAO {
    /**
     * The new group is to be added.
     * @param group
     * The ID (positive Long number) will be generated in DB.
     * String name: unique name of the group.
     * String password: password to enter the group.
     * Timestamp time_to_start: time for participants to start.
     * Can be changed by adding time intervals.
     * String description: description of the group
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
     * String name: name of the group.
     * String password: password of the group.
     * Long userID: The ID of user to join the group.
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
     * @param groupMember:
     * group_id: ID of group, that will contain this user.
     * user_id: ID of user, that will be added to the group.
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
}
