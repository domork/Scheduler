package domork.MySchedule.util;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;

import java.sql.Timestamp;

public interface Validator {

    /**
     * Checks, if name has at least 1 char.
     * Maximum length of text is 63 chars.
     * Name cannot start with space ' '.
     * @param name is the String to check.
     */
    void nameCheck(String name);

    /**
     * Checks, if the ID is not null, bigger than MINVALUE
     * and smaller than MAXVALUE.
     * If ID is negative, it must be for test purposes only.
     * @param ID .
     */
    void idCheck(Long ID);

    /**
     * Checks, if password is not nul.
     * Password must have at least 1 char.
     * Maximum is 32 chars.
     * @param pass the password to check.
     */
    void passwordCheck (String pass);


    /**
     * Checks, if the time_to_start is not null and is a valid time.
     * @param time with given vars.
     */
    void timeCheck(Timestamp time);

    /**
     * Checks, if the group credentials is not null and:
     *  password fills the passwordCheck() requirements.
     *  userID fills the idCheck() requirements.
     *  username fills the nameText() requirements.
     * @param groupCredentials with given vars.
     */
    void groupCredentialsCheck (GroupCredentials groupCredentials);

    /**
     * Checks, if the group groupMember is not null and:
     *  group_id        |
     *  user_id         |-> fill the idCheck() requirements.
     *  group_user_UUID |
     *  color fills the colorCheck() requirements.
     *  name fills the nameText() requirements.
     * @param groupMember with given vars.
     */
    void groupMemberCheck (GroupMember groupMember);

    /**
     * checks the role on these non-null-values:
     * 'user','moderator','admin'.
     *
     * @param role to be checked.
     */
    void groupRoleCheck (String role);

    /**
     * checks the validity of UUID
     * @param s the UUID
     */
    void UUIDCheck (String s);

    /**
     * checks the validity of color:
     * it must be a hex value (e.x. #d2a6f1)
     * @param color to be checked.
     */
    void colorCheck (String color);

    /**
     * @param t with given vars:
     *  group_user_UUID fill the idCheck() requirements.
     *  color fills the colorCheck() requirements.
     *  name fills the nameText() requirements.
     *  both times are not null and
     *  time_start < time_end.
     */
    void timeIntervalByUserCheck(TimeIntervalByUser t);
}
