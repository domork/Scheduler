export class GroupMember {
  group_id: number;
  user_id: number;
  group_user_UUID: string;
  color: string;


  constructor(group_id: number, user_id: number, group_user_UUID: string, color: string) {
    this.group_id = group_id;
    this.user_id = user_id;
    this.group_user_UUID = group_user_UUID;
    this.color = color;
  }
}
