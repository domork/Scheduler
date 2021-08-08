export class Group {
  id: number;
  name:String;
  password: string;
  time_to_start: Date;
  description:string;
  group_user_uuid:string;


  constructor(id: number, name: String, password: string, time_to_start: Date, description: string, group_user_uuid: string) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.time_to_start = time_to_start;
    this.description = description;
    this.group_user_uuid = group_user_uuid;
  }
}
