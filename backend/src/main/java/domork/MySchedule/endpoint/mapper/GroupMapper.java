package domork.MySchedule.endpoint.mapper;

import domork.MySchedule.endpoint.dto.GroupCredentialsDto;
import domork.MySchedule.endpoint.dto.GroupDto;
import domork.MySchedule.endpoint.dto.GroupMemberDto;
import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.endpoint.entity.GroupCredentials;
import domork.MySchedule.endpoint.entity.GroupMember;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupMapper {
    public Group dtoToEntity (GroupDto groupDto) {
        return new Group(groupDto.getID(),groupDto.getName(), groupDto.getPassword(), groupDto.getTime_to_start(), groupDto.getDescription());
    }
    public GroupDto entityToDto (Group group) {
        return new GroupDto(group.getID(),group.getName(),group.getPassword(),group.getTime_to_start(),group.getDescription());
    }
    public List<GroupDto> entityListToDtoList(List<Group> list){
        List <GroupDto> groupDtoList = new ArrayList<>();
        for (Group group:list){
            groupDtoList.add(entityToDto(group));
        }
        return groupDtoList;
    }

    public GroupCredentials dtoToGroupCredentials(GroupCredentialsDto groupCredentialsDto){
        return new GroupCredentials(groupCredentialsDto.getName(), groupCredentialsDto.getPassword(), groupCredentialsDto.getUserID());
    }

    public GroupMemberDto groupMemberToDto (GroupMember groupMember){
        return new GroupMemberDto(groupMember.getGroup_id(), groupMember.getUser_id(), groupMember.getGroup_user_UUID());
    }
}
