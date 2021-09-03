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

    /**
     * Submits the message from user when something is wrong. (hopefully)
     * @param s is the message to save.
     * @throws ValidationException if message is null or longer than 255 chars.
     */
    void reportAnIssue (String s);
}
