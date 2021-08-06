package domork.MySchedule.endpoint.dto;

public class GroupMemberDto {
    Long group_id;
    Long user_id;
    String group_user_UUID;
    String color;

    public GroupMemberDto(Long group_id, Long user_id, String group_user_UUID, String color) {
        this.group_id = group_id;
        this.user_id = user_id;
        this.group_user_UUID = group_user_UUID;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public GroupMemberDto(Long group_id) {
        this.group_id = group_id;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getGroup_user_UUID() {
        return group_user_UUID;
    }

    public void setGroup_user_UUID(String group_user_UUID) {
        this.group_user_UUID = group_user_UUID;
    }

    @Override
    public String toString() {
        return "GroupMemberDto{" +
                "group_id=" + group_id +
                ", user_id=" + user_id +
                ", group_user_UUID='" + group_user_UUID + '\'' +
                '}';
    }
}
