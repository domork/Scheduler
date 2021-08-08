export class TimeIntervalByUser {
  group_user_UUID: string;
  time_start: Date|undefined;
  time_end: Date|undefined;
  color: string
  name: string


  constructor(group_user_UUID: string, time_start: Date, time_end: Date, color: string, name: string) {
    this.group_user_UUID = group_user_UUID;
    this.time_start = time_start;
    this.time_end = time_end;
    this.color = color;
    this.name = name;
  }

}
