export class GroupMember {
  group_id: number;
  user_id: number;
  group_user_UUID: string;
  color: string;
  name: string;
  description:string;


  constructor(group_id: number, user_id: number, group_user_UUID: string, color: string, name: string, description: string) {
    this.group_id = group_id;
    this.user_id = user_id;
    this.group_user_UUID = group_user_UUID;
    this.color = color;
    this.name = name;
    this.description = description;
  }
}
