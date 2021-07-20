package domork.MySchedule.endpoint.entity;

import java.sql.Timestamp;

public class Group {
    private Long ID;
    private String name;
    private String password;
    private Timestamp time_to_start;
    private String description;

    public Group(Long ID, String name, String password,
                 Timestamp time_to_start, String description) {
        this.ID = ID;
        this.name = name;
        this.password = password;
        this.time_to_start = time_to_start;
        this.description = description;
    }

    public Group() {
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
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

    public Timestamp getTime_to_start() {
        return time_to_start;
    }

    public void setTime_to_start(Timestamp time_to_start) {
        this.time_to_start = time_to_start;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Group{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", time_to_start=" + time_to_start +
                ", description='" + description + '\'' +
                '}';
    }
}
