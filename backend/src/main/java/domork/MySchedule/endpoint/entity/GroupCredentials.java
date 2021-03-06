package domork.MySchedule.endpoint.entity;

public class GroupCredentials {
    private String name;
    private String password;
    private Long userID;
    private String username;

    public GroupCredentials(String name, String password, Long userID, String username) {
        this.name = name;
        this.password = password;
        this.userID = userID;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GroupCredentials() {
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "GroupCredentials{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", userID=" + userID +
                ", username='" + username + '\'' +
                '}';
    }
}
