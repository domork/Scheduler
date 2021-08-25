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

  //getTime() + 24 * 60 * 60 * 1000 => next day.
  currentDate = new Date(new Date().getTime());
  // @ts-ignore
  id: number = +this.route.snapshot.paramMap.get('id');
  arr: any[] = [];
  usersInGroup: Map<string, string> = new Map();
  user_names: string[] = [];
  user_colors: string[] = [];
  map = new Map();
  addForm: TimeIntervalByUser = new TimeIntervalByUser('', undefined, undefined, undefined, undefined);
  parsedTime_start: string = '';
  parsedTime_end: string = '';
  fetchedData = false;

  constructor(private groupService: GroupService, private route: ActivatedRoute) {
  }

  //please be patient :)
  ngOnInit(): void {
    this.getGroupParticipantsForCurrentDay();
    console.log(this.currentDate.toLocaleTimeString(navigator.language, {hour: '2-digit', minute: '2-digit'}));
  }

  getGroupParticipantsForCurrentDay(): void {
    this.fetchedData = false;
    this.groupService.getGroupParticipantsForDay
    (this.currentDate, this.id).subscribe(data => {
      this.arr = data;
      this.addIntervalsToMap();
      this.fetchedData = true;
    }, error => {
      console.log(error);
      this.fetchedData = true;

    })
  }

  setInMap(hour: number, temp: any[]): void {
    let hourValueInMap = this.map.get(hour);
    let tempArr: any[] = [];
    if (hourValueInMap) {
      hourValueInMap.forEach((data: any) => {

        if (data[1] !== temp[1]) {
          tempArr.push(data);
        }

      })
      this.map.delete(hour);
      this.map.set(hour, [[temp[0], temp[1]]]);
      tempArr.forEach((data: any) => {
        this.map.get(hour).push(data);
      })

    } else this.map.set(hour, [temp]);

  }

  addIntervalsToMap(): void {
    //loop through arr

    for (let i = 0; i < this.arr.length; i++) {
      if (this.arr[i].time_end) {
        let hourEnd = new Date(this.arr[i].time_end).getHours();
        let hourStart = new Date(this.arr[i].time_start).getHours();
        let hoursDifference = hourEnd - hourStart;
        let minuteEnd = new Date(this.arr[i].time_end).getMinutes();
        let minuteStart = new Date(this.arr[i].time_start).getMinutes();

        let minuteStartInFirstQuarter = minuteStart <= 15;
        let minuteEndInFirstQuarter = minuteEnd <= 15;
        let minuteEndInLastQuarter = minuteEnd > 45;
        /*
        * 0 full
        * 1 cut top
        * 2 cut bottom
        * 3 cut both
        * */

        if (hoursDifference > 1) {


          this.setInMap((hourEnd - 1) * 100, [0, this.arr[i].color]);
          this.setInMap((hourEnd - 1) * 100 + 1, [0, this.arr[i].color]);
          for (let k = 2; k < hoursDifference; k++) {
            let hour = hourEnd - k;
            this.setInMap(hour * 100, [0, this.arr[i].color]);
            this.setInMap(hour * 100 + 1, [0, this.arr[i].color]);
          }

        }
        if (hoursDifference === 0) {

          if (!minuteStartInFirstQuarter) {
            //15:20-15:45 => 15:30-16:00
            this.setInMap(hourStart * 100 + 1, [3, this.arr[i].color])

          } else {
            //15:14-15:45 => 15:00-15:30
            this.setInMap(hourStart * 100, [!minuteEndInLastQuarter ? 3 : 1, this.arr[i].color])

            if (minuteEndInLastQuarter)
              this.setInMap(hourStart * 100 + 1, [2, this.arr[i].color])
          }
        } else if (hoursDifference === 1 && minuteEndInFirstQuarter) {
          if (!minuteStartInFirstQuarter) {
            //15:20-15:45 => 15:30-16:00
            this.setInMap(hourStart * 100 + 1, [3, this.arr[i].color])

          } else {
            //15:14-15:45 => 15:00-15:30
            this.setInMap(hourStart * 100, [1, this.arr[i].color])
            this.setInMap(hourStart * 100 + 1, [2, this.arr[i].color])
          }
        } else {
          if (minuteStartInFirstQuarter) {
            this.setInMap(hourStart * 100, [1, this.arr[i].color])
          }
          this.setInMap(hourStart * 100 + 1, [minuteStartInFirstQuarter ? 0 : 1, this.arr[i].color])

          if (minuteEndInFirstQuarter)
            this.setInMap((hourEnd - 1) * 100 + 1, [2, this.arr[i].color]);
          else if (!minuteEndInLastQuarter) {
            this.setInMap(hourEnd * 100, [2, this.arr[i].color])
          } else {
            this.setInMap(hourEnd * 100, [0, this.arr[i].color])
            this.setInMap(hourEnd * 100 + 1, [2, this.arr[i].color])
          }
        }
      }

      if (!this.usersInGroup.has(this.arr[i].color)) {

        this.usersInGroup.set(this.arr[i].color, this.arr[i].name);
      }

    }
    this.user_names = [...this.usersInGroup.values()];
    this.user_colors = [...this.usersInGroup.keys()];
  }


  hourIsActive(hour: number, color: string): any {
    let hourInMap = this.map.get(hour);

    if (hourInMap) {
      for (let i = 0; i < hourInMap.length; i++) {
        if (hourInMap[i][1] === color) {
          let text = `background-color: ${color}; ${hour % 2 == 0 ? 'opacity: 0.696; ' : ''}`;
          switch (hourInMap[i][0]) {
            case 0:
              return text + 'border-bottom-width: 0;';
            case 1:
              return text + ` border-radius: 7px 7px 0 0; border-bottom-width: 0;`;
            case 2:
              return text + ` border-radius: 0 0 7px 7px; `;
            case 3:
              return text + ` border-radius: 10px;`;
          }


        }
      }
    }
    return '';
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
    this.parsedTime_start = '';
    this.parsedTime_end = '';
    this.usersInGroup = new Map<string, string>();
    this.user_colors = [];
    this.user_names = [];
  }

  setPrevDay(): void {
    this.resetData();
    this.currentDate.setDate(this.currentDate.getDate() - 1);
    this.getGroupParticipantsForCurrentDay();
  }

  setTime(hour: number): void {
    let tempDate = new Date(this.currentDate.getTime());
    tempDate.setHours(Math.floor(hour / 100));
    tempDate.setMinutes(hour % 2 == 0 ? 0 : 30);
    tempDate.setSeconds(0);
    if (this.addForm.time_start) {
      if (this.addForm.time_start.getTime() > tempDate.getTime()) {
        this.addForm.time_start = tempDate;
      } else {
        this.addForm.time_end = tempDate;
      }

    } else {
      this.addForm.time_start = tempDate;
    }
    if (this.addForm.time_start)
      this.parsedTime_start = this.addForm.time_start.toLocaleTimeString(navigator.language, {
        hour: '2-digit',
        minute: '2-digit'
      });

    if (this.addForm.time_end)
      this.parsedTime_end = this.addForm.time_end.toLocaleTimeString(navigator.language, {
        hour: '2-digit',
        minute: '2-digit'
      });

  }

  timepickerToAddForm(s: string, i: number): void {
    let hour = Number(s.substring(0, 2));
    let minute = Number(s.substring(s.length - 2, s.length));
    switch (i) {
      case 0: {
        if (!this.addForm.time_start)
          this.addForm.time_start = this.currentDate;
        this.addForm.time_start.setHours(hour);
        this.addForm.time_start.setMinutes(minute);
        break;
      }
      case 1: {
        if (!this.addForm.time_end)
          this.addForm.time_end = this.currentDate;
        this.addForm.time_end.setHours(hour)
        this.addForm.time_end.setMinutes(minute)
      }
    }
  }


  closeForm(): void {
    this.addForm.time_start = undefined;
    this.addForm.time_end = undefined;
    this.parsedTime_start = '';
    this.parsedTime_end = '';
  }

  onSubmit(): void {

    this.groupService.addNewInterval(this.id, this.addForm).subscribe(
      data => {
        this.resetData();
        this.getGroupParticipantsForCurrentDay();
        this.addForm.time_end = undefined;
        this.addForm.time_start = undefined;
      }, error => {
        console.log(error);
      }
    );

  }

}
