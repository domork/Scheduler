import {Timestamp} from "rxjs";
export class Group {
  id: number;
  name:String;
  password: string;
  time_to_start: Date;
  description:string;

  constructor(id: number,name:String, password: string,
              time_to_start: Date, description:string
              ) {
   this.id = id;
   this.name = name;
   this.password = password;
   this.time_to_start = time_to_start;
   this.description = description;
  }
}
