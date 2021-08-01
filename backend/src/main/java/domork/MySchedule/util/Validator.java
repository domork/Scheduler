package domork.MySchedule.util;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;

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
     *  not null.
     *  name fills the nameText() requirements.
     *  password fills the passwordCheck() requirements.
     *  time_to_start is a valid time.
     *  description fills the nameText() requirements.
     * @param group with given vars.
     */
    void groupCheck(Group group);

    /**
     * Checks, if the group credentials is not null and:
     *  name fills the nameText() requirements.
     *  password fills the passwordCheck() requirements.
     *  userID fills the idCheck() requirements.
     * @param groupCredentials with given vars.
     */
    void groupCredentialsCheck (GroupCredentials groupCredentials);

    /**
     * Checks, if the group groupMember is not null and:
     *  group_id        |
     *  user_id         |-> fill the idCheck() requirements.
     *  group_user_UUID |
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
}
