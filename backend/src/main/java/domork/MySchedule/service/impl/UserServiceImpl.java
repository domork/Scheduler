package domork.MySchedule.service.impl;

import domork.MySchedule.exception.ValidationException;
import domork.MySchedule.persistance.UserDAO;
import domork.MySchedule.service.UserService;
import domork.MySchedule.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;
    private final UserDAO userDAO;

    public UserServiceImpl(Validator validator, UserDAO userDAO) {
        this.validator = validator;
        this.userDAO = userDAO;
    }

    @Override
    public boolean userExists(Long ID) {
        LOGGER.trace("userExists({})", ID);
        return userDAO.userExists(ID);
    }

    @Override
    public void reportAnIssue(String s) {
        LOGGER.trace("reportAnIssue({})", s);
        if (s==null)
            throw new ValidationException("Message must be given.");
        else if (s.length()>255)
            throw new ValidationException("Message can have max. 255 characters.");
        userDAO.reportAnIssue(s);
    }
}
