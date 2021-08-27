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

        validator.groupCheck(group);
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

        validator.groupCredentialsCheck(groupCredentials);
        return companyDAO.joinGroupByNameAndPassword(groupCredentials);
    }

    @Override
    public List<Group> getGroupsByID(Long ID) {
        LOGGER.trace("getGroupsByID({})", ID);
        validator.idCheck(ID);
        List<Group> list =  companyDAO.getGroupsByID(ID);
        for (Group g: list){
            calculateNextMeetingByGroupId(g.getID());
        }
        return list;
    }

    @Override
    public Group getGroupByName(String name) {
        LOGGER.trace("getGroupByName({})", name);

        //validator.nameCheck(name);
        return companyDAO.getGroupByName(name);
    }

    @Override
    public GroupMember addMemberToTheGroup(GroupMember groupMember) {
        LOGGER.trace("addMemberToTheGroup({})", groupMember);

        validator.groupMemberCheck(groupMember);

        List<Group> groups = this.getGroupsByID(groupMember.getUser_id());

        for (Group group : groups) {
            if (group.getID().equals(groupMember.getGroup_id()))
                throw new ValidationException("This user is already in this group.");
        }

        return companyDAO.addMemberToTheGroup(groupMember);
    }

    @Override
    public boolean addMemberRoleToTheGroup(String UUID, String role) {
        LOGGER.trace("addMemberToTheGroup({}, {})", UUID, role);

        validator.groupRoleCheck(role);
        validator.UUIDCheck(UUID);

        return companyDAO.addMemberRoleToTheGroup(UUID, role);
    }

    @Override
    public List<TimeIntervalByUser> getGroupInfoForSpecificDate(Long groupID, LocalDate date) {
        LOGGER.trace("getGroupInfoForSpecificDate({}, {})", groupID, date);

        validator.idCheck(groupID);
        Long userID = this.getUserPrinciple().getId();
        List<Group> groups = this.getGroupsByID(userID);

        for (Group group : groups) {
            if (group.getID().equals(groupID))
                return companyDAO.getGroupInfoForSpecificDate(groupID, date);
        }
        throw new ValidationException("Not a member of group/ no such a group.");


    }

    @Override
    public TimeIntervalByUser addNewInterval(TimeIntervalByUser timeIntervalByUser, Long groupID) {
        LOGGER.trace("addNewInterval({})", timeIntervalByUser);
        timeIntervalByUser.setGroup_user_UUID(this.getUUIDOfCurrentUserByGroupId(groupID));

        validator.timeIntervalByUserCheck(timeIntervalByUser);
        timeIntervalByUser.setTime_start(truncateToMinutes(timeIntervalByUser.getTime_start()));
        timeIntervalByUser.setTime_end(truncateToMinutes(timeIntervalByUser.getTime_end()));

        List<TimeIntervalByUser> list = companyDAO.getMemberInfoForSpecificDate(timeIntervalByUser.getGroup_user_UUID(),
                timeIntervalByUser.getTime_start().toLocalDateTime().toLocalDate());
        Union u = new Union();


        long addStartTime = timeIntervalByUser.getTime_start().getTime();
        long addEndTime = timeIntervalByUser.getTime_end().getTime();
        Interval addInterval = new Interval(addStartTime, addEndTime);

        Interval interval;
        long startTime;
        long endTime;

        if (list.isEmpty()) {
            return companyDAO.addNewInterval(timeIntervalByUser);
        } else if (list.size() == 1) {
            startTime = list.get(0).getTime_start().getTime();
            endTime = list.get(0).getTime_end().getTime();
            interval = new Interval(startTime, endTime);

            Set result = addInterval.union(interval);
            if (!result.isContinuous())
                return companyDAO.addNewInterval(timeIntervalByUser);
            else {
                deleteInterval(list.get(0).getGroup_user_UUID(), list.get(0).getTime_end());
                timeIntervalByUser.setTime_start(parseFromEpochToTimestamp(((Interval) result).getLower()));
                timeIntervalByUser.setTime_end(parseFromEpochToTimestamp(((Interval) result).getUpper()));
                return companyDAO.addNewInterval(timeIntervalByUser);
            }
        } else {
            u.union(addInterval);
            for (TimeIntervalByUser t : list) {
                startTime = t.getTime_start().getTime();
                endTime = t.getTime_end().getTime();
                interval = new Interval(startTime, endTime);
                u.union(interval);
            }
            deleteInterval(timeIntervalByUser.getGroup_user_UUID(),
                    Timestamp.valueOf(timeIntervalByUser.getTime_end().toLocalDateTime().withHour(0).withMinute(0)));

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
        companyDAO.deleteInterval(UUID, date);
    }

    @Override
    public void leaveGroup(Long groupID) {
        companyDAO.leaveGroup(groupID);
    }

    @Override
    public String getUUIDOfCurrentUserByGroupId(Long groupID) {
        return companyDAO.getUUIDOfCurrentUserByGroupId(groupID);
    }

    @Override
    public Timestamp calculateNextMeetingByGroupId(Long groupID) {
        return companyDAO.calculateNextMeetingByGroupId(groupID);
    }

    @Override
    public GroupMember getGroupMemberInfoByUUID(String UUID) {
        return companyDAO.getGroupMemberInfoByUUID(UUID);
    }

    @Override
    public void updateGroupMemberInfoByUUID(String UUID, String color, String name) {
        validator.colorCheck(color);
        companyDAO.updateGroupMemberInfoByUUID(UUID, color, name);
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
