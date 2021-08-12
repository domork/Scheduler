package domork.MySchedule.util;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;

public interface Validator {

    /**
     * Checks, if the text contain only allowed
     * characters, such as [a-zA-Z0-9- .:,] AND has at least 1 char.
     * Maximum length of text is 63 chars.
     *
     * @param name is the String to check.
     */
    void nameCheck(String name);

    /**
     * Checks, if the ID is not null, bigger than 0
     * and smaller than MAXVALUE.
     * @param ID .
     */
    void idCheck(Long ID);

    /**
     * Checks, if password is not null and
     * has only allowed chars, such as
     * [a-zA-Z0-9-+* /.:,] .
     * Password must have at least 1 char.
     * Maximum is 32 chars.
     * @param pass the password to check.
     */
    void passwordCheck (String pass);

    /**
     * Checks, if the group is not null and :
     *  name fills the nameText() requirements.
     *  password fills the passwordCheck() requirements.
     *  time_to_start is a valid time.
     *                if it's not null.
     *  description fills the nameText() requirements.
     *              if it's not null.
     * @param group with given vars.
     */
    void groupCheck(Group group);

    /**
     * Checks, if the group credentials is not null and:
     *  name fills the nameText() requirements.
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
