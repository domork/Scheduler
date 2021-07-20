package domork.MySchedule.service.impl;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.service.GroupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Override
    public Group createNewGroup(Group group) {
        return null;
    }

    @Override
    public Group joinGroupByNameAndPassword(GroupCredentials groupCredentials) {
        return null;
    }

    @Override
    public List<Group> getGroupsByID(Long ID) {
        return null;
    }
}
