package domork.MySchedule.endpoint;

import domork.MySchedule.endpoint.dto.GroupCredentialsDto;
import domork.MySchedule.endpoint.dto.GroupDto;
import domork.MySchedule.endpoint.mapper.GroupMapper;
import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.exception.PersistenceException;
import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RequestMapping(GroupEndpoint.BASE_URL)
@RestController
public class GroupEndpoint {
    static final String BASE_URL = "/groups";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @Autowired
    public GroupEndpoint (GroupService groupService, GroupMapper groupMapper){
        this.groupService=groupService;
        this.groupMapper=groupMapper;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GroupDto> createNewGroup (@RequestBody GroupDto groupDto){
        LOGGER.info("POST NEW GROUP: " + BASE_URL + "/{}", groupDto);

        try {
            return null;

        }
     catch (ValidationException e) {
        LOGGER.warn("POST GROUP: (" + groupDto + ") THROWS VALIDATION_EXCEPTION ({})", e.getMessage(), e);
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    } catch (PersistenceException e) {
        LOGGER.error("POST GROUP: (" + groupDto + ") THROWS PERSISTENCE_EXCEPTION ({})", e.getMessage(), e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/join")
    public ResponseEntity<GroupDto> joinGroupByNameAndPassword(@RequestBody GroupCredentialsDto groupCredentialsDto) {
        LOGGER.info("GET GROUP BY NAME AND PASSWORD /{}" + BASE_URL, groupCredentialsDto);
        try {
            return null;

        }
        catch (NotFoundException e) {
            LOGGER.warn("GET GROUP BY NAME AND PASSWORD  (" + groupCredentialsDto + ") THROWS NOT_FOUND_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (ValidationException e) {
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

        LOGGER.info("GET GROUPS BY ID /{}" + BASE_URL, "TODO");
        try {
            return null;
        }
        catch (NotFoundException e) {
            LOGGER.warn("GET GROUPS BY ID   (" + "TODO" + ") THROWS NOT_FOUND_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (ValidationException e) {
            LOGGER.warn("GET GROUPS BY ID   (" + "TODO" + ") THROWS VALIDATION_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (PersistenceException e) {
            LOGGER.error("GET GROUPS BY ID   (" + "TODO" + ") THROWS PERSISTENCE_EXCEPTION ({})", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
