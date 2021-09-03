package domork.MySchedule.persistance.impl;

import domork.MySchedule.exception.NotFoundException;
import domork.MySchedule.persistance.UserDAO;
import domork.MySchedule.endpoint.entity.User;
import domork.MySchedule.security.services.UserPrinciple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Service
public class UserDAOImpl implements UserDAO {
    private static final String TABLE_NAME = "users";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final JdbcTemplate jdbcTemplate;

    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean userExists(Long ID) {
        LOGGER.trace("userExists({})", ID);
        final String sql = "SELECT * FROM " + TABLE_NAME
                + " WHERE ID =" + ID;
        List<User> users = jdbcTemplate.query(sql, this::mapRow);
        if (users.isEmpty())
            throw new NotFoundException("User with ID +" + ID + " was not found");
        return true;
        }

    @Override
    public void reportAnIssue(String s) {
        final String sql = "INSERT INTO issue_messages (message, id,name) VALUES (?,?,?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS);
            int paramIndex = 1;
            statement.setString(paramIndex++, s);
            statement.setLong(paramIndex++, this.getUserPrinciple().getId());
            statement.setString(paramIndex, this.getUserPrinciple().getUsername());
            return statement;
        });
    }

    private User mapRow (ResultSet resultSet, int i) throws SQLException{
        final User user = new User();
        user.setID(resultSet.getLong("id"));
        user.setPassword(resultSet.getString("password"));
        user.setUsername(resultSet.getString("username"));
        return user;
    }

    private UserPrinciple getUserPrinciple() {
        return ((UserPrinciple) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal());
    }
}
