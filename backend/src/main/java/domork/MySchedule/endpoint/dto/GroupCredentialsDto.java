package domork.MySchedule.endpoint.dto;

public class GroupCredentialsDto {
    private String name;
    private String password;
    private Long userID;
    public GroupCredentialsDto(String name, String password, Long userID) {
        this.name = name;
        this.password = password;
        this.userID=userID;
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
                '}';
    }
}
