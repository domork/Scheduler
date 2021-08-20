export class Group {
  id: number;
  name:String;
  password: string;
  time_to_start: Date;
  description:string;
  group_user_uuid:string;
  numOfRdyPeople:number|undefined;
  numOfAllPeople:number|undefined;
  iPickedTime: boolean|undefined;


  constructor(id: number, name: String, password: string, time_to_start: Date, description: string, group_user_uuid: string, numOfRdyPeople: number | undefined, numOfAllPeople: number | undefined, iPickedTime: boolean | undefined) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.time_to_start = time_to_start;
    this.description = description;
    this.group_user_uuid = group_user_uuid;
    this.numOfRdyPeople = numOfRdyPeople;
    this.numOfAllPeople = numOfAllPeople;
    this.iPickedTime = iPickedTime;
  }
}
