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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Date;
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
        return companyDAO.getGroupsByID(ID);
    }

    @Override
    public Group getGroupByName(String name) {
        LOGGER.trace("getGroupByName({})", name);

        validator.nameCheck(name);
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
    public TimeIntervalByUser addNewInterval(TimeIntervalByUser timeIntervalByUser) {
        LOGGER.trace("addNewInterval({})", timeIntervalByUser);

        validator.timeIntervalByUserCheck(timeIntervalByUser);


        return companyDAO.addNewInterval(timeIntervalByUser);
    }

    private UserPrinciple getUserPrinciple(){
        return ((UserPrinciple) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal());
    }

}
