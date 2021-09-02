package domork.MySchedule.util.impl;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;
import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.util.Validator;
import org.apache.tomcat.jni.Time;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Service
public class ValidatorImpl implements Validator {
    private final static Pattern textPattern = Pattern.compile("[a-zA-Z0-9- .:,]*");
    private final static Pattern passPattern = Pattern.compile("[a-zA-Z0-9-+*/ .:,]*");
    private final static Pattern colorPattern = Pattern.compile("#[a-fA-F0-9]{1,6}");
    private final static Pattern UUIDPattern = Pattern.compile
            ("[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}");


    @Override
    public void nameCheck(String name) {
        if (name != null) {
                if (name.length() > 63 || name.length() < 1)
                    throw new ValidationException("Name must have the length between 1 and 63");
                else if (name.charAt(0) == ' ')
                    throw new ValidationException("Name cannot start with space (' ')");

        }
    }

    @Override
    public void idCheck(Long ID) {
        if (ID == null)
            throw new ValidationException("ID must be some positive integer");
    }

    @Override
    public void passwordCheck(String pass) {
        if (pass != null) {
            if ((pass.length() > 32 || pass.length() <= 1))
                throw new ValidationException("Password must have the length between 1 and 32.");
        } else
            throw new ValidationException("Password was not provided.");

    }


    @Override
    public void groupCredentialsCheck(GroupCredentials groupCredentials) {
        passwordCheck(groupCredentials.getPassword());
        idCheck(groupCredentials.getUserID());
    }

    @Override
    public void groupMemberCheck(GroupMember groupMember) {
        idCheck(groupMember.getGroup_id());
        idCheck(groupMember.getUser_id());
        UUIDCheck(groupMember.getGroup_user_UUID());
        colorCheck(groupMember.getColor());
        nameCheck(groupMember.getName());
    }

    @Override
    public void groupRoleCheck(String role) {
        if (!(role.equals("user") || role.equals("moderator") || role.equals("admin"))) {
            throw new ValidationException("Role is not correct.");
        }
    }

    @Override
    public void UUIDCheck(String s) {
        if (s != null) {
            if (!UUIDPattern.matcher(s).matches())
                throw new ValidationException("UUID has wrong format.");

        }
    }

    @Override
    public void colorCheck(String color) {
        if (color != null && !colorPattern.matcher(color).matches())
            throw new ValidationException("Color has wrong format.");
    }


    @Override
    public void timeIntervalByUserCheck(TimeIntervalByUser t) {
        if (t == null) {
            throw new ValidationException("Time interval is null.");
        }
        UUIDCheck(t.getGroup_user_UUID());
        colorCheck(t.getColor());
        //nameCheck(t.getName());

        if (t.getTime_start() == null && t.getTime_end() == null)
            throw new ValidationException("Time interval was not given.");

        else if (t.getTime_start() == null)
            throw new ValidationException("Start time was not given.");

        else if (t.getTime_end() == null)
            throw new ValidationException("End time was not given.");

        else if (t.getTime_end().before(t.getTime_start()))
            throw new ValidationException("End time is before start time.");

        else if (t.getTime_start().toLocalDateTime().getDayOfMonth() != t.getTime_end().toLocalDateTime().getDayOfMonth())
            throw new ValidationException("Interval must start and end at the same time.");

    }

    @Override
    public void descriptionCheck(String s) {
        if (s!=null && s.length()>254)
            throw new ValidationException("Description is too long. Maximum is a length of 254. ");
    }

}
