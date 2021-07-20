package domork.MySchedule.endpoint.entity;

public class User {
    private Long ID;
    private String password;
    private String username;

    public User(Long ID, String password, String username) {
        this.ID = ID;
        this.password = password;
        this.username = username;
    }

    public User() {
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
        return "User{" +
                "ID=" + ID +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
