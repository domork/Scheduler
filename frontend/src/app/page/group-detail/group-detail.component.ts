import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../service/group.service";
import {ActivatedRoute} from "@angular/router";
import {TimeIntervalByUser} from "../../utils/dto/time-interval-by-user";

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent implements OnInit {

  currentDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);

  // @ts-ignore
  id: number = +this.route.snapshot.paramMap.get('id');
  arr: any[] = [];
  usersInGroup: Map<string, string> = new Map();
  user_names: string[] = [];
  user_colors: string[] = [];
  map = new Map();
  addForm: TimeIntervalByUser = new TimeIntervalByUser('', undefined, undefined, '', '');
  parsedTime_start: string|undefined='';
  parsedTime_end: string|undefined='';
  form: any = {};

  constructor(private groupService: GroupService, private route: ActivatedRoute) {
  }

  //please be patient :)
  ngOnInit(): void {
    this.getGroupParticipantsForCurrentDay();
  }

  getGroupParticipantsForCurrentDay(): void {
    this.groupService.getGroupParticipantsForDay
    (this.currentDate, this.id).subscribe(data => {
      console.log(data);
      this.arr = data;
      let currentUser = data.pop();
      if (currentUser) {
        this.addForm.group_user_UUID = currentUser.group_user_UUID;
        this.addForm.color = currentUser.color;
        this.addForm.name = currentUser.name;
      }
      this.addIntervalsToMap();
    }, error => {
      console.log(error);
    })
  }

  setInMap(hour: number, temp: any[]): void {
    let hourValueInMap = this.map.get(hour);
    if (hourValueInMap) {
      this.map.get(hour).push([temp[0], temp[1]])
    } else {
      this.map.set(hour, [[temp[0], temp[1]]]);
    }
  }

  addIntervalsToMap(): void {
    //loop through arr
    for (let i = 0; i < this.arr.length; i++) {
      let hourEnd = new Date(this.arr[i].time_end).getHours();
      let hourStart = new Date(this.arr[i].time_start).getHours();
      let hours = hourEnd - hourStart;
      let minuteEnd = new Date(this.arr[i].time_end).getMinutes();
      let minuteStart = new Date(this.arr[i].time_start).getMinutes();
      if (hours > 0) {
        this.setInMap(hourEnd, [minuteEnd / 60, this.arr[i].color])
        this.setInMap(hourStart, [(minuteStart / 60) - 1, this.arr[i].color])
      } else if (hours == 0) {
        this.setInMap(hourStart, [[(minuteStart / 60) - 1, (minuteEnd / 60) - 1], this.arr[i].color])
      }
      if (hours > 1) {
        for (let k = 1; k < hours; k++) {
          let hour = hourEnd - k;
          this.setInMap(hour, [1, this.arr[i].color]);
        }
      }

      if (!this.usersInGroup.has(this.arr[i].color)) {
        this.usersInGroup.set(this.arr[i].color, this.arr[i].name);
      }
    }
    // for (let [key, value] of this.map) {
    //   console.log(key + " = " + value);
    // }

    this.user_names = [...this.usersInGroup.values()];
    this.user_colors = [...this.usersInGroup.keys()];
  }


  hourIsActive(hour: number, color: string): any[] {
    let hourInMap = this.map.get(hour);
    if (hourInMap) {
      for (let i = 0; i < hourInMap.length; i++) {
        if (hourInMap[i][1] === color) {
          // console.log(hour, hourInMap[i]);
          let text, startPercentage, endPercentage;
          if (hourInMap[i][0] == 1) {
            text = `background-color: ${color}`;
          } else if (Array.isArray(hourInMap[i][0])) {
            startPercentage = (hourInMap[i][0][0] + 1) * 100;
            endPercentage = (hourInMap[i][0][1] + 1) * 100;
            text = `background: linear-gradient(180deg,
             #FFFFFF ${startPercentage}%, ${color} ${startPercentage}% ${endPercentage}%, #FFFFFF ${endPercentage}%);`;

          } else {
            startPercentage = (hourInMap[i][0]) * 100;

            if (startPercentage < 0) {
              text = `background: linear-gradient(180deg, #FFFFFF ${startPercentage += 100}%, ${color} 0%);`;
            } else {
              text = `background: linear-gradient(180deg, ${color} ${startPercentage}%, #FFFFFF ${startPercentage}%);`;
            }
          }
          return [true, text];
        }
      }
    }
    return [false, 0];
  }

  setNextDay(): void {
    this.resetData();
    this.currentDate.setDate(this.currentDate.getDate() + 1);
    this.getGroupParticipantsForCurrentDay();
  }

  resetData(): void {
    this.arr = [];
    this.user_names = [];
    this.map = new Map();
    this.addForm.time_end = undefined
    this.addForm.time_start = undefined
    this.parsedTime_start='';
    this.parsedTime_end='';
  }

  setPrevDay(): void {
    this.resetData();
    this.currentDate.setDate(this.currentDate.getDate() - 1);
    this.getGroupParticipantsForCurrentDay();
  }

  setTime(hour: number): void {
    let tempDate = new Date(this.currentDate.getTime());
    tempDate.setHours(hour);
    tempDate.setMinutes(0);
    if (this.addForm.time_start) {
      if (this.addForm.time_start.getTime() > tempDate.getTime()) {
        this.addForm.time_start = tempDate;
      } else {
        this.addForm.time_end = tempDate;
      }
    } else {
      this.addForm.time_start = tempDate;
    }
    console.log(this.addForm.time_start.toLocaleTimeString())
    console.log(this.addForm.time_end)
    if (this.addForm.time_start)
    this.parsedTime_start= this.addForm.time_start.toLocaleTimeString().substring(0, 5);

    if (this.addForm.time_end)
    this.parsedTime_end= this.addForm.time_end.toLocaleTimeString().substring(0, 5);
  }


  onSubmit(): void {

    console.log(this.addForm);
     this.groupService.addNewInterval(this.id, this.addForm).subscribe(
       data => {
         this.arr.push(data);
         this.getGroupParticipantsForCurrentDay();
         this.addForm.time_end = undefined;
         this.addForm.time_start = undefined;
       }, error => {
         console.log(error);
       }
     );

  }

}
