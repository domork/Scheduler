package domork.MySchedule.security.repository;

import domork.MySchedule.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<domork.MySchedule.security.model.User> findByUsername(String username);
    boolean existsByUsername(String username);


}
