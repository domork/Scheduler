package domork.MySchedule.service.impl;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.endpoint.entity.TimeIntervalByUser;
import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.persistance.GroupDAO;
import domork.MySchedule.security.services.UserPrinciple;
import domork.MySchedule.service.GroupService;
import domork.MySchedule.util.Validator;
import domork.MySchedule.util.interval.Interval;
import domork.MySchedule.util.interval.Set;
import domork.MySchedule.util.interval.Union;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service

public class GroupServiceImpl implements GroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GroupDAO companyDAO;
    private final Validator validator;

    public GroupServiceImpl(GroupDAO companyDAO, Validator validator) {
        this.companyDAO = companyDAO;
        this.validator = validator;
    }

    @Override
    public Group createNewGroup(Group group) {
        LOGGER.trace("createNewGroup({})", group);

        /* ***********  Validation  *********** */
        //validator.timeCheck(group.getTime_to_start());
        validator.nameCheck(group.getName());
        validator.passwordCheck(group.getPassword());
        validator.descriptionCheck(group.getDescription());
        /* ********  End of Validation  ******* */

        // If there are no groups with given name, then it can be added.
        try {
            getGroupByName(group.getName());
        } catch (NotFoundException e) {
            return companyDAO.createNewGroup(group);
        }
        throw new ValidationException("Group with given name already exists");
    }

    @Override
    public GroupMember joinGroupByNameAndPassword(GroupCredentials groupCredentials) {
        LOGGER.trace("joinGroupByNameAndPassword({})", groupCredentials);
        /* ***********  Validation  *********** */
        validator.groupCredentialsCheck(groupCredentials);
        /* ********  End of Validation  ******* */
        return companyDAO.joinGroupByNameAndPassword(groupCredentials);
    }

    @Override
    public List<Group> getGroupsByID(Long ID) {
        LOGGER.trace("getGroupsByID({})", ID);
        /* ***********  Validation  *********** */
        validator.idCheck(ID);
        /* ********  End of Validation  ******* */
        List<Group> list = companyDAO.getGroupsByID(ID);
        //Calculate the next meeting every time the groups are fetched.
        //It would be preferable to run one time at the end of the day.
        for (Group g : list) {
            g.setTime_to_start(calculateNextMeetingByGroupId(g.getID()));
        }
        return list;
    }

    @Override
    public void getGroupByName(String name) {
        LOGGER.trace("getGroupByName({})", name);
        /* ***********  Validation  *********** */
        validator.nameCheck(name);
        /* ********  End of Validation  ******* */
        companyDAO.getGroupByName(name);
    }

    @Override
    public GroupMember addMemberToTheGroup(GroupMember groupMember) {
        LOGGER.trace("addMemberToTheGroup({})", groupMember);
        /* ***********  Validation  *********** */
        validator.groupMemberCheck(groupMember);

        // The check, that the user is already in the group will be done in DAO
        /* ********  End of Validation  ******* */

        return companyDAO.addMemberToTheGroup(groupMember);
    }

    @Override
    public boolean addMemberRoleToTheGroup(String UUID, String role) {
        LOGGER.trace("addMemberToTheGroup({}, {})", UUID, role);
        /* ***********  Validation  *********** */
        validator.groupRoleCheck(role);
        validator.UUIDCheck(UUID);
        /* ********  End of Validation  ******* */
        return companyDAO.addMemberRoleToTheGroup(UUID, role);
    }

    @Override
    public List<TimeIntervalByUser> getGroupInfoForSpecificDate(Long groupID, LocalDate date) {
        LOGGER.trace("getGroupInfoForSpecificDate({}, {})", groupID, date);
        /* ***********  Validation  *********** */
        validator.idCheck(groupID);
        getUUIDOfCurrentUserByGroupId(groupID);
        /* ********  End of Validation  ******* */
        return companyDAO.getGroupInfoForSpecificDate(groupID, date);
    }

    @Override
    public TimeIntervalByUser addNewInterval(TimeIntervalByUser timeIntervalByUser, Long groupID) {
        LOGGER.trace("addNewInterval({})", timeIntervalByUser);
        /* ***********  Validation  *********** */
        //since it is not possible yet to add a new interval for other person, I do this check.
        timeIntervalByUser.setGroup_user_UUID(this.getUUIDOfCurrentUserByGroupId(groupID));
        validator.timeIntervalByUserCheck(timeIntervalByUser);
        /* ********  End of Validation  ******* */

        //I truncate the time to minutes, so it's easier for search/delete.
        timeIntervalByUser.setTime_start(truncateToMinutes(timeIntervalByUser.getTime_start()));
        timeIntervalByUser.setTime_end(truncateToMinutes(timeIntervalByUser.getTime_end()));

        //Fetch the day when the new interval should be added.
        List<TimeIntervalByUser> list = companyDAO.getMemberInfoForSpecificDate(timeIntervalByUser.getGroup_user_UUID(),
                timeIntervalByUser.getTime_start().toLocalDateTime().toLocalDate());
        Union u = new Union();


        long addStartTime = timeIntervalByUser.getTime_start().getTime();
        long addEndTime = timeIntervalByUser.getTime_end().getTime();
        Interval addInterval = new Interval(addStartTime, addEndTime);

        Interval interval;
        long startTime;
        long endTime;
        //If no interval from this user is at this day.
        if (list.isEmpty()) {
            return companyDAO.addNewInterval(timeIntervalByUser);
        }
        //If one interval from this user is at this day.
        else if (list.size() == 1) {
            startTime = list.get(0).getTime_start().getTime();
            endTime = list.get(0).getTime_end().getTime();
            interval = new Interval(startTime, endTime);

            Set result = addInterval.union(interval);
            //If the intervals are not intersected, e.x. one is 08:00-12:00 and second 15:30-16:00.
            if (!result.isContinuous())
                return companyDAO.addNewInterval(timeIntervalByUser);
            else {
                //Otherwise, delete saved interval and insert one interval.
                deleteInterval(list.get(0).getGroup_user_UUID(), list.get(0).getTime_end());
                timeIntervalByUser.setTime_start(parseFromEpochToTimestamp(((Interval) result).getLower()));
                timeIntervalByUser.setTime_end(parseFromEpochToTimestamp(((Interval) result).getUpper()));
                return companyDAO.addNewInterval(timeIntervalByUser);
            }
        }
        // else => multiple intervals are in the day.
        else {
            //Create one union of all intervals.
            u.union(addInterval);
            for (TimeIntervalByUser t : list) {
                startTime = t.getTime_start().getTime();
                endTime = t.getTime_end().getTime();
                interval = new Interval(startTime, endTime);
                u.union(interval);
            }

            //Delete all intervals of this day by this user.
            deleteInterval(timeIntervalByUser.getGroup_user_UUID(),
                    Timestamp.valueOf(timeIntervalByUser.getTime_end().toLocalDateTime().withHour(0).withMinute(0)));

            //Add from union all intervals back.
            for (Interval i : u.getList()) {
                timeIntervalByUser.setTime_start(parseFromEpochToTimestamp(i.getLower()));
                timeIntervalByUser.setTime_end(parseFromEpochToTimestamp(i.getUpper()));
                companyDAO.addNewInterval(timeIntervalByUser);
            }
        }
        this.calculateNextMeetingByGroupId(groupID);
        return timeIntervalByUser;
    }

    @Override
    public void deleteInterval(String UUID, Timestamp date) {
        date = truncateToMinutes(date);
        System.out.println(companyDAO.getGroupMemberInfoByUUID(UUID).getUser_id());
        /* ***********  Validation  *********** */
        if (!companyDAO.getGroupMemberInfoByUUID(UUID).getUser_id().equals(this.getUserPrinciple().getId()))
            throw new ValidationException("You cannot delete other interval yet.");
        validator.UUIDCheck(UUID);
        /* ********  End of Validation  ******* */
        companyDAO.deleteInterval(UUID, date);
    }

    @Override
    public void leaveGroup(Long groupID) {
        /* ***********  Validation  *********** */
        if (groupID == null || groupID == 0)
            throw new ValidationException("To leave a group provide the group's ID please");
        /* ********  End of Validation  ******* */
        companyDAO.leaveGroup(groupID);
    }

    @Override
    public String getUUIDOfCurrentUserByGroupId(Long groupID) {
        /* ***********  Validation  *********** */
        if (groupID == null || groupID == 0)
            throw new ValidationException("To get a UUID provide the group's ID please");
        /* ********  End of Validation  ******* */
        return companyDAO.getUUIDOfCurrentUserByGroupId(groupID);
    }

    @Override
    public Timestamp calculateNextMeetingByGroupId(Long groupID) {
        /* ***********  Validation  *********** */
        if (groupID == null || groupID == 0)
            throw new ValidationException("To calculate the next meeting provide the group's ID please");
        /* ********  End of Validation  ******* */
        return companyDAO.calculateNextMeetingByGroupId(groupID);
    }

    @Override
    public GroupMember getGroupMemberInfoByUUID(String UUID) {
        /* ***********  Validation  *********** */
        validator.UUIDCheck(UUID);
        /* ********  End of Validation  ******* */
        return companyDAO.getGroupMemberInfoByUUID(UUID);
    }

    @Override
    public void updateGroupMemberInfoByUUID(String UUID, String color, String name) {
        /* ***********  Validation  *********** */
        validator.colorCheck(color);
        validator.UUIDCheck(UUID);
        validator.nameCheck(name);
        /* ********  End of Validation  ******* */
        companyDAO.updateGroupMemberInfoByUUID(UUID, color, name);
    }

    @Override
    public boolean groupByIdAlreadyExist(Long groupID) {
        return companyDAO.groupByIdAlreadyExist(groupID);
    }

    private UserPrinciple getUserPrinciple() {
        return ((UserPrinciple) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal());
    }

    private Timestamp truncateToMinutes(Timestamp date) {
        LocalDateTime localDateTime = date.toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
        return Timestamp.valueOf(localDateTime);
    }

    private Timestamp parseFromEpochToTimestamp(long i) {
        return new Timestamp(i);
    }
}
