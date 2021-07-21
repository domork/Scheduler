package domork.MySchedule.service;
import domork.MySchedule.exception.*;
public interface UserService {

    /**
     * Checks if the user with a given ID exists
     * @param ID of user to check
     * @return true if user is saved in DB.
     * @throws ValidationException if ID is null.
     */
    boolean userExists (Long ID);
}
