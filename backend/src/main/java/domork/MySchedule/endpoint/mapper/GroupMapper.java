package domork.MySchedule.endpoint.mapper;

import domork.MySchedule.endpoint.dto.GroupDto;
import domork.MySchedule.endpoint.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {
    public Group dtoToEntity (GroupDto groupDto) {
        return new Group(groupDto.getID(),groupDto.getName(), groupDto.getPassword(), groupDto.getTime_to_start(), groupDto.getDescription());
    }
    public GroupDto entityToDto (Group group) {
        return new GroupDto(group.getID(),group.getName(),group.getPassword(),group.getTime_to_start(),group.getDescription());
    }
}
