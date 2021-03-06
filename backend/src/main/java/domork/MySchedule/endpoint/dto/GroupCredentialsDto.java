package domork.MySchedule.endpoint.dto;

public class GroupCredentialsDto {
    private String name;
    private String password;
    private Long userID;
    private String userName;

    public GroupCredentialsDto(String name, String password, Long userID, String userName) {
        this.name = name;
        this.password = password;
        this.userID = userID;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public GroupCredentialsDto() {
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
        return "GroupCredentialsDto{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", userID=" + userID +
                ", userName='" + userName + '\'' +
                '}';
    }
}
