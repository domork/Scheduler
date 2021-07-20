package domork.MySchedule.endpoint.dto;

import java.sql.Timestamp;

public class UserDto {
    private Long ID;
    private String password;
    private String username;

    public UserDto(Long ID, String password, String username) {
        this.ID = ID;
        this.password = password;
        this.username = username;
    }

    public UserDto() {
    }

    public Long getID() {

        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "ID=" + ID +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
