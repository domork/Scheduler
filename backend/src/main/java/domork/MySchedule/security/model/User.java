package domork.MySchedule.security.model;

import org.hibernate.annotations.NaturalId;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        })

})
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min=3, max = 50)
    private String username;

    @NotBlank
    @Size(min=6, max = 100)
    private String password;

    @NotBlank
    @DateTimeFormat
    private Timestamp registrationTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<domork.MySchedule.security.model.Role> roles = new HashSet<>();

    public User() {}

    public User(Long id, String username, String password, Timestamp registrationTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.registrationTime = registrationTime;
    }

    public User(String username, String password, Timestamp registrationTime) {
        this.username = username;
        this.password = password;
        this.registrationTime = registrationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<domork.MySchedule.security.model.Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<domork.MySchedule.security.model.Role> roles) {
        this.roles = roles;
    }
}
