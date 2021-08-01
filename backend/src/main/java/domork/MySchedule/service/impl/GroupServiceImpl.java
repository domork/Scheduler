package domork.MySchedule.service.impl;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.persistance.GroupDAO;
import domork.MySchedule.service.GroupService;
import domork.MySchedule.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
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

        return companyDAO.addMemberToTheGroup(groupMember);
    }

    @Override
    public boolean addMemberRoleToTheGroup(String UUID, String role) {
        LOGGER.trace("addMemberToTheGroup({}, {})", UUID, role);

        validator.groupRoleCheck(role);
        validator.UUIDCheck(UUID);

        return companyDAO.addMemberRoleToTheGroup(UUID,role);
    }
}
