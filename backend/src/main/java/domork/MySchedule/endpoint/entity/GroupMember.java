package domork.MySchedule.endpoint.entity;

public class GroupMember {
    Long group_id;
    Long user_id;
    String group_user_UUID;
    String color;
    String name;

    public GroupMember(Long group_id, Long user_id, String group_user_UUID, String color, String name) {
        this.group_id = group_id;
        this.user_id = user_id;
        this.group_user_UUID = group_user_UUID;
        this.color = color;
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public GroupMember() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "group_id=" + group_id +
                ", user_id=" + user_id +
                ", group_user_UUID='" + group_user_UUID + '\'' +
                ", color='" + color + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
