package domork.MySchedule.service;

import domork.MySchedule.endpoint.dto.GroupCredentialsDto;
import domork.MySchedule.endpoint.dto.GroupDto;
import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import org.springframework.web.bind.annotation.RequestBody;
import domork.MySchedule.exception.*;
import java.util.List;

public interface GroupService {
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
     * @throws ValidationException when the group doesn't
     * fill the requirements (check them in the Validator).
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
     * @throws ValidationException when the groupCredentials don't
     * fill the requirements (check them in the Validator).
     */
    Group joinGroupByNameAndPassword(GroupCredentials groupCredentials);

    /**
     * Provides all groups for authenticated user, in which user
     * participates.
     * @param ID of user
     * @return all groups, where ID is presented.
     * @throws ValidationException when the ID doesn't
     * fill the requirements (check them in the Validator).
     */
    List<Group> getGroupsByID(Long ID);

}
