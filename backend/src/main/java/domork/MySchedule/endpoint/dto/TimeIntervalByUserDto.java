package domork.MySchedule.endpoint.dto;

import java.sql.Timestamp;

public class TimeIntervalByUserDto {
    private Long user_unique_group_id;
    private Timestamp time_start;
    private Timestamp time_end;

    public TimeIntervalByUserDto(Long user_unique_group_id,
                                 Timestamp time_start, Timestamp time_end) {
        this.user_unique_group_id = user_unique_group_id;
        this.time_start = time_start;
        this.time_end = time_end;
    }

    public TimeIntervalByUserDto() {
    }

    public Long getUser_unique_group_id() {
        return user_unique_group_id;
    }

    public void setUser_unique_group_id(Long user_unique_group_id) {
        this.user_unique_group_id = user_unique_group_id;
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
                "user_unique_group_id=" + user_unique_group_id +
                ", time_start=" + time_start +
                ", time_end=" + time_end +
                '}';
    }
}
