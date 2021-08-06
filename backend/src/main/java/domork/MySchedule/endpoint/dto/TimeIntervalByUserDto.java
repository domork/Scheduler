package domork.MySchedule.endpoint.dto;

import java.sql.Timestamp;

public class TimeIntervalByUserDto {
    private String group_user_UUID;
    private Timestamp time_start;
    private Timestamp time_end;
    private String color;

    public TimeIntervalByUserDto() {
    }

    public TimeIntervalByUserDto(String group_user_UUID, Timestamp time_start, Timestamp time_end, String color) {
        this.group_user_UUID = group_user_UUID;
        this.time_start = time_start;
        this.time_end = time_end;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGroup_user_UUID() {
        return group_user_UUID;
    }

    public void setGroup_user_UUID(String group_user_UUID) {
        this.group_user_UUID = group_user_UUID;
    }

    public Timestamp getTime_start() {
        return time_start;
    }

    public void setTime_start(Timestamp time_start) {
        this.time_start = time_start;
    }

    public Timestamp getTime_end() {
        return time_end;
    }

    public void setTime_end(Timestamp time_end) {
        this.time_end = time_end;
    }

    @Override
    public String toString() {
        return "TimeIntervalByUserDto{" +
                "group_user_UUID='" + group_user_UUID + '\'' +
                ", time_start=" + time_start +
                ", time_end=" + time_end +
                ", color='" + color + '\'' +
                '}';
    }
}
