package domork.MySchedule.endpoint;

import domork.MySchedule.endpoint.entity.Group;
import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.security.model.Role;
import domork.MySchedule.security.model.RoleName;
import domork.MySchedule.security.model.User;
import domork.MySchedule.security.repository.*;
import domork.MySchedule.security.jwt.JwtProvider;
import domork.MySchedule.security.message.request.LoginForm;
import domork.MySchedule.security.message.request.SignUpForm;
import domork.MySchedule.security.message.response.JwtResponse;
import domork.MySchedule.security.message.response.ResponseMessage;
import domork.MySchedule.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AppEndpoint {

    @Autowired
    GroupService groupService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    public AppEndpoint(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()), new Timestamp(System.currentTimeMillis()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).
                orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
    }

    @PostMapping("/demo")
    public ResponseEntity<?> demoUser() {
        String username = "test_user";
        Random rand = new Random();
        int int_random = Math.negateExact(Math.abs(rand.nextInt(1000000)));

        for (int i = 0; i < 50; i++) {
            if (i == 49)
                return new ResponseEntity<>(new ResponseMessage("Could not create demo."), HttpStatus.INSUFFICIENT_STORAGE);
            if (userRepository.existsByUsername(username + int_random))
                int_random = Math.negateExact(Math.abs(rand.nextInt(1000000)));
            else break;
        }
        username += int_random;
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        //pass length of 24
        for (int i = 0; i < 24; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        String pass = sb.toString();

        // Creating user's account
        User user = new User((long) int_random, "test_user" + int_random,
                encoder.encode(pass), new Timestamp(System.currentTimeMillis()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_DEMO).
                orElseThrow(() -> new RuntimeException("Fail! -> Cause: Demo Role not found."));

        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);

        ResponseEntity<?> response = this.authenticateUser(new LoginForm(username, pass));

        long groupID = Math.negateExact(Math.abs(rand.nextLong() % 1000000));
        while (groupService.groupByIdAlreadyExist(groupID))
            groupID = Math.negateExact(Math.abs(rand.nextLong() % 1000000));

        String groupName = "Random Group #";

        //try 50 times.
        for (int i = 0; i < 50; i++) {
            try {
                groupService.getGroupByName("Small "+groupName + int_random);
                int_random = rand.nextInt(1000000);
            } catch (NotFoundException e) {
                groupName += int_random;
                break;
            }
        }


        groupService.createNewGroup(new Group(null, "Small "+groupName, pass, new Timestamp(System.currentTimeMillis()), "Test description. Hi!", null));
        groupService.createNewGroup(new Group(null, "Medium "+groupName, pass, new Timestamp(System.currentTimeMillis()), "Test description. Hi!", null));
        groupService.createNewGroup(new Group(null, "Large "+groupName, pass, new Timestamp(System.currentTimeMillis()), "Test description. Hi!", null));

        return response;
    }

}