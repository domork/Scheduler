package domork.MySchedule.persistance;

import domork.MySchedule.exception.*;

public interface UserDAO {
    /**
     * Checks if the user with a given ID exists
     * @param ID of user to check
     * @return true if user is saved in DB.
     * @throws NotFoundException when no user with given ID exists.
     */
    boolean userExists (Long ID);

    /**
     * Submits the message from user when something is wrong. (hopefully)
     * @param s is the message to save.
     */
    void reportAnIssue (String s);
}
