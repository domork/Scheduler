import {Component, OnInit, Inject} from '@angular/core';
import {GroupService} from "../../service/group.service";
import {ActivatedRoute} from "@angular/router";
import {TimeIntervalByUser} from "../../utils/dto/time-interval-by-user";

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent implements OnInit {

   dayTime:number[] = [800, 801, 900, 901, 1000, 1001, 1100, 1101, 1200, 1201, 1300,
    1301, 1400, 1401, 1500, 1501, 1600, 1601, 1700, 1701, 1800, 1801, 1900, 1901, 2000, 2001, 2100, 2101, 2200, 2201];

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
  Math:any;

  // @ts-ignore
  constructor(private groupService: GroupService, private route: ActivatedRoute) {
    this.Math=Math;
  }


  //please be patient :)
  ngOnInit(): void {

    this.getGroupParticipantsForCurrentDay();
  }

  getGroupParticipantsForCurrentDay(): void {
    this.fetchedData = false;
    this.groupService.getGroupParticipantsForDay
    (this.currentDate, this.id).subscribe(data => {
      this.arr = data;
      for (let i = 0; i < this.arr.length; i++) {

        this.addIntervalsToMap(this.arr[i]);

      }
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
        if (data && data[1] !== temp[1]) {
          tempArr.push(data);
        }

      })
      this.map.delete(hour);
      this.map.set(hour, [[temp[0], temp[1], temp[2]]]);
      tempArr.forEach((data: any) => {
        this.map.get(hour).push(data);
      })

    } else this.map.set(hour, [temp]);

  }

  addIntervalsToMap(interval: TimeIntervalByUser): void {

    if (interval.time_end) {
      let hourEnd = new Date(interval.time_end).getHours();
      let hourStart = new Date(interval.time_start as Date).getHours();
      let hoursDifference = hourEnd - hourStart;
      let minuteEnd = new Date(interval.time_end).getMinutes();
      let minuteStart = new Date(interval.time_start as Date).getMinutes();

      let bgc = `background-color: ${interval.color}; `;
      let text0 = bgc + ' border-bottom-width: 0;';
      let text1 = bgc + ' border-radius: 7px 7px 0 0; border-bottom-width: 0;';
      let text2 = bgc + ' border-radius: 0 0 7px 7px; ';
      let text3 = bgc + ' border-radius: 10px;';
      if (interval.color?.endsWith('temp')) {
        bgc = ' border-width: initial; border-color: black; '
        let bTop = ' border-top: none; border-bottom-color: black; border-bottom-width: thick; ';
        let bBottom = ' border-bottom: none; border-top-color: black; border-top-width: thick; ';
        text0 += bgc + bTop + bBottom;
        text1 += bgc + bBottom;
        text2 += bgc + bTop;
        text3 += bgc;
      }

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
        this.setInMap((hourEnd - 1) * 100, [0, interval.color, text0]);
        this.setInMap((hourEnd - 1) * 100 + 1, [0, interval.color, text0]);
        for (let k = 2; k < hoursDifference; k++) {
          let hour = hourEnd - k;
          this.setInMap(hour * 100, [0, interval.color, text0]);
          this.setInMap(hour * 100 + 1, [0, interval.color, text0]);
        }

      }
      if (hoursDifference === 0) {

        if (!minuteStartInFirstQuarter) {
          //15:20-15:45 => 15:30-16:00
          this.setInMap(hourStart * 100 + 1, [3, interval.color, text3])

        } else {
          //15:14-15:45 => 15:00-15:30
          this.setInMap(hourStart * 100, [!minuteEndInLastQuarter ? 3 : 1, interval.color, !minuteEndInLastQuarter ? text3 : text1])

          if (minuteEndInLastQuarter)
            this.setInMap(hourStart * 100 + 1, [2, interval.color, text2])
        }
      } else if (hoursDifference === 1 && minuteEndInFirstQuarter) {
        if (!minuteStartInFirstQuarter) {
          //15:20-15:45 => 15:30-16:00
          this.setInMap(hourStart * 100 + 1, [3, interval.color, text3])

        } else {
          //15:14-15:45 => 15:00-15:30
          this.setInMap(hourStart * 100, [1, interval.color, text1])
          this.setInMap(hourStart * 100 + 1, [2, interval.color, text2])
        }
      } else {
        if (minuteStartInFirstQuarter) {
          this.setInMap(hourStart * 100, [1, interval.color, text1])
        }
        this.setInMap(hourStart * 100 + 1, [minuteStartInFirstQuarter ? 0 : 1, interval.color, minuteStartInFirstQuarter ? text0 : text1])

        if (minuteEndInFirstQuarter)
          this.setInMap((hourEnd - 1) * 100 + 1, [2, interval.color, text2]);
        else if (!minuteEndInLastQuarter) {
          this.setInMap(hourEnd * 100, [2, interval.color, text2])
        } else {
          this.setInMap(hourEnd * 100, [0, interval.color, text0])
          this.setInMap(hourEnd * 100 + 1, [2, interval.color, text2])
        }
      }
    }

    if (!this.usersInGroup.has(<string>interval.color?.replace('temp', ''))) {

      if (interval.color && interval.name) {
        this.usersInGroup.set(interval.color, interval.name);
      }
    }


    this.user_names = [...this.usersInGroup.values()];
    this.user_colors = [...this.usersInGroup.keys()];
  }


  hourIsActive(hour: number, color: string): any {
    let hourInMap = this.map.get(hour);
    if (hourInMap) {
      for (let i = 0; i < hourInMap.length; i++) {
        if (hourInMap[i][1].replace('temp', '') === color) {
          return hourInMap[i][2];
        }
      }
    }
    return '';
  }

  deleteButtonIsOn(color: string, hour: number): boolean {
    if (this.user_colors[0]!==color)
      return false;
    let hourInMap = this.map.get(hour);
    if (hourInMap) {
      if (hourInMap[0] && hourInMap[0][1] === color && ((hourInMap[0][0] === 1 || hourInMap[0][0] === 3)) || hour==800){
        return true;}

    }
    return false;
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

  deleteAllTempIntervalsInMap(): void {

    for (const [key, value] of this.map.entries()) {
      let currValue = [];
      let toUpdateFlag = false;
      for (let i = 0; i < value.length; i++) {
        if (value[i][1]?.endsWith('temp')) {
          toUpdateFlag = true;
        } else
          currValue.push(value[i]);
      }
      if (toUpdateFlag) {
        if (currValue.length)
          this.map.set(key, currValue);
        else
          this.map.delete(key);
      }
    }

  }

  public delay(ms: number): Promise<any> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  setTime(hour: number): void {
    (async () => {
      this.deleteAllTempIntervalsInMap()
      await this.delay(1);

      let tempDate = new Date(this.currentDate.getTime());
      tempDate.setHours(Math.floor(hour / 100));
      tempDate.setMinutes(hour % 2 == 0 ? 0 : 30);
      tempDate.setSeconds(0);
      if (this.addForm.time_start) {
        if (this.addForm.time_start.getTime() === tempDate.getTime()) {
          this.closeForm();
          return;
        }
        if (this.addForm.time_start.getTime() > tempDate.getTime()) {
          this.addForm.time_start = tempDate;
        } else {
          this.addForm.time_end = tempDate;

        }

      } else {
        this.addForm.time_start = tempDate;
        this.addForm.time_end = new Date(tempDate.getTime() + 30 * 60000);
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
      this.addIntervalsToMap(new TimeIntervalByUser(this.arr[0].group_user_UUID, this.addForm.time_start, this.addForm.time_end, this.arr[0].color + 'temp', this.arr[0].name));
    })()
  }

  timepickerToAddForm(s: string, i: number): void {
    (async () => {
      this.deleteAllTempIntervalsInMap()
      await this.delay(1);
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
      this.addIntervalsToMap(new TimeIntervalByUser(this.arr[0].group_user_UUID, this.addForm.time_start, this.addForm.time_end, this.arr[0].color + 'temp', this.arr[0].name));

    })();
  }

  deleteInterval(time: number): void {
    this.setTime(time);
    let hour = Math.floor(time / 100);
    let minute = (time % 2 == 0 ? 0 : 30) + 1;
    let intervalDay = new Date(this.currentDate);

    intervalDay.setMinutes(minute);
    intervalDay.setHours(hour);

    let currentUUID = this.arr[0].group_user_UUID;
    for (let i = 0; i < this.arr.length; i++) {
      if (this.arr[i].group_user_UUID !== currentUUID)
        break;
      if (new Date(this.arr[i].time_start).getTime() <= intervalDay.getTime() && intervalDay.getTime() <= new Date(this.arr[i].time_end).getTime()) {

        this.groupService.deleteInterval(currentUUID, new Date(this.arr[i].time_end)).subscribe(data => {
          this.resetData();
          this.getGroupParticipantsForCurrentDay();
        });
      }
    }
  }

  closeForm(): void {
    this.addForm.time_start = undefined;
    this.addForm.time_end = undefined;
    this.parsedTime_start = '';
    this.parsedTime_end = '';
    this.deleteAllTempIntervalsInMap();
  }

  onSubmit(): void {

    this.groupService.addNewInterval(this.id, this.addForm).subscribe(
      data => {
        this.resetData();
        this.getGroupParticipantsForCurrentDay();
      }, error => {
        console.log(error);
      }
    );

  }

}
