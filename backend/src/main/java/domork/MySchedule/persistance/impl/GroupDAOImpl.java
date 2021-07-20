package domork.MySchedule.persistance.impl;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.persistance.GroupDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupDAOImpl implements GroupDAO {
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
