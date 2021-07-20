package domork.MySchedule.persistance;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
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
    Group joinGroupByNameAndPassword(GroupCredentials groupCredentials);

    /**
     * Provides all groups for authenticated user, in which user
     * participates.
     * @param ID of user
     * @return all groups, where ID is presented.
     * @throws PersistenceException when something goes wrong with
     * the DB. (e.x. no connection).
     */
    List<Group> getGroupsByID(Long ID);
}
