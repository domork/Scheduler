package domork.MySchedule.endpoint.entity;

public class GroupCredentials {
    private String name;
    private String password;
    private Long userID;

    public GroupCredentials(String name, String password, Long userID) {
        this.name = name;
        this.password = password;
        this.userID=userID;
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
                '}';
    }
}
