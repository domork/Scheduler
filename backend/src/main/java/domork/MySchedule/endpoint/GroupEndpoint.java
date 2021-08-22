package domork.MySchedule.endpoint;

import domork.MySchedule.endpoint.dto.GroupCredentialsDto;
import domork.MySchedule.endpoint.dto.GroupDto;
import domork.MySchedule.endpoint.dto.GroupMemberDto;
import domork.MySchedule.endpoint.dto.TimeIntervalByUserDto;
import domork.MySchedule.endpoint.mapper.GroupMapper;
import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.exception.PersistenceException;
import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.security.services.UserPrinciple;
import domork.MySchedule.service.GroupService;
import domork.MySchedule.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequestMapping(GroupEndpoint.BASE_URL)
@RestController
public class GroupEndpoint {
    static final String BASE_URL = "/groups";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final UserService userService;

    @Autowired
    public GroupEndpoint(UserService userService, GroupService groupService, GroupMapper groupMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GroupDto> createNewGroup(@RequestBody GroupDto groupDto) {
        LOGGER.info("POST NEW GROUP: " + BASE_URL + "/{}", groupDto);

        try {
            return new ResponseEntity<>(groupMapper.entityToDto
                    (groupService.createNewGroup
                            (groupMapper.dtoToEntity(groupDto))), HttpStatus.OK);

        } catch (ValidationException e) {
            LOGGER.warn("POST GROUP: (" + groupDto + ") THROWS VALIDATION_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (PersistenceException e) {
            LOGGER.error("POST GROUP: (" + groupDto + ") THROWS PERSISTENCE_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/join")
    public ResponseEntity<GroupMemberDto> joinGroupByNameAndPassword(@RequestBody GroupCredentialsDto groupCredentialsDto) {
        LOGGER.info("JOIN GROUP BY NAME AND PASSWORD /{}" + BASE_URL, groupCredentialsDto);
        try {
            groupCredentialsDto.setUserID
                    (getUserID());
            return new ResponseEntity<>(groupMapper.groupMemberToDto(
                    groupService.joinGroupByNameAndPassword
                            (groupMapper.dtoToGroupCredentials
                                    (groupCredentialsDto))), HttpStatus.OK);
        } catch (NotFoundException e) {
            LOGGER.warn("GET GROUP BY NAME AND PASSWORD  (" + groupCredentialsDto + ") THROWS NOT_FOUND_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ValidationException e) {
            LOGGER.warn("GET GROUP BY NAME AND PASSWORD  (" + groupCredentialsDto + ") THROWS VALIDATION_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (PersistenceException e) {
            LOGGER.error("GET GROUP BY NAME AND PASSWORD  (" + groupCredentialsDto + ") THROWS PERSISTENCE_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<GroupDto>> getGroupsByID() {
        Long userID = getUserID();
        LOGGER.info("GET GROUPS BY USER ID /{}", userID);

        try {
            //having PreAuthorize prevents the null pointer exception
            userService.userExists(userID);
            return new ResponseEntity<>(groupMapper.entityListToDtoList(groupService.getGroupsByID(userID)), HttpStatus.OK);
        } catch (NotFoundException e) {
            LOGGER.warn("GET GROUPS BY ID   (" + userID + ") THROWS NOT_FOUND_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ValidationException e) {
            LOGGER.warn("GET GROUPS BY ID   (" + userID + ") THROWS VALIDATION_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (PersistenceException e) {
            LOGGER.error("GET GROUPS BY ID   (" + userID + ") THROWS PERSISTENCE_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<List<TimeIntervalByUserDto>>
    getGroupInfoForSpecificDate(@PathVariable("id") Long id,
                                @RequestParam(value = "date")
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate date) {
        LOGGER.info("GET GROUP INFO BY GROUP ID /{}", id);

        return new ResponseEntity<>(
                groupMapper.timeIntervalByUserListToDto
                        (groupService.getGroupInfoForSpecificDate(id, date)), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/{id}")
    public ResponseEntity<TimeIntervalByUserDto>
    addNewInterval(@PathVariable("id") Long id,
                   @RequestBody TimeIntervalByUserDto timeIntervalByUserDto) {
        LOGGER.info("POST NEW INTERVAL ({}) IN GROUP ID /{}", timeIntervalByUserDto, id);

        return new ResponseEntity<>
                (groupMapper.timeIntervalByUserToDto(
                        groupService.addNewInterval(
                                groupMapper.dtoToTimeIntervalByUserTo(timeIntervalByUserDto),id))
                        , HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping(value = "/")
    public ResponseEntity<Boolean> deleteInterval(@RequestParam(value = "date")
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                                                  @RequestParam(value = "UUID") String UUID) {

        LOGGER.info("DELETE INTERVAL WITH UUID({}) ON THIS DATE/{}", UUID, date);
        Timestamp a=  Timestamp.valueOf(date);
        groupService.deleteInterval(UUID, a);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    private Long getUserID() {
        return ((UserPrinciple) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal()).getId();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/{id}/leave")
    public ResponseEntity<Boolean> leaveGroup(@PathVariable("id") Long id){
        LOGGER.info("LEAVE GROUP WITH ID({}) ", id);
        groupService.leaveGroup(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/{id}/memberInfo")
    public ResponseEntity<GroupMemberDto> getGroupMemberInfoByUUID(@PathVariable("id") String UUID){
        LOGGER.info("GET GROUP MEMBER INFO BY GROUP ID ({}) ", UUID);

        return new ResponseEntity<>(groupMapper.groupMemberToDto(groupService.getGroupMemberInfoByUUID(UUID)), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/{id}/memberInfo")
    public ResponseEntity<Boolean> updateGroupMemberInfoByUUID(@PathVariable("id") String UUID,
                                                                      @RequestParam(value = "color") String color,
                                                                      @RequestParam(value = "name") String name){
        LOGGER.info("UPDATE GROUP MEMBER INFO BY GROUP ID ({}), WITH COLOR {} AND NAME {}", UUID, color, name);
        groupService.updateGroupMemberInfoByUUID(UUID,color,name);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
