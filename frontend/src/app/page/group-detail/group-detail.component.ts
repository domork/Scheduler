import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent implements OnInit {

  currentDate = (new Date).toLocaleDateString();

  tempDate1 = {hours: 10, minutes: 15};
  tempDate2 = {hours: 15, minutes: 30};
  tempDate3 = {hours: 13, minutes: 15};
  tempDate4 = {hours: 13, minutes: 45};
  tempDate5 = {hours: 8, minutes: 20};
  tempDate6 = {hours: 9, minutes: 45};
  tempDate7 = {hours: 14, minutes: 15};
  tempDate8 = {hours: 17, minutes: 45};
  person1: any[] = [this.tempDate1, this.tempDate2, this.tempDate5, this.tempDate6];
  person2: any[] = [this.tempDate3, this.tempDate4, this.tempDate7, this.tempDate8];

  arr: any[] = [[this.person1, '#d05252'], [this.person2, '#6561ea']];
  map = new Map();

  //please be patient :)
  ngOnInit(): void {
    //loop through arr
    console.log(new Date().toISOString());
    for (let i = 0; i < this.arr.length; i++) {
      let iLength = this.arr[i][0].length;
      //loop through a person
      for (let j = iLength - 1; j >= 0; j -= 2) {
        let hourEnd = this.arr[i][0][j].hours;
        let hourStart = this.arr[i][0][j - 1].hours;
        let hours = hourEnd - hourStart;
        let minuteEnd = this.arr[i][0][j].minutes;
        let minuteStart = this.arr[i][0][j - 1].minutes;

        if (hours > 0 && minuteEnd > 0) {
          this.setInMap(hourEnd, [minuteEnd / 60, this.arr[i][1]])
          this.setInMap(hourStart, [(minuteStart / 60) - 1, this.arr[i][1]])
        } else if (hours == 0) {
          this.setInMap(hourStart, [[(minuteStart / 60) - 1, (minuteEnd / 60) - 1], this.arr[i][1]])
        }

        if (hours > 1) {
          for (let k = 1; k < hours; k++) {
            let hour = hourEnd - k;
            this.setInMap(hour, [1, this.arr[i][1]])
          }
        }
      }
    }
  }


  setInMap(hour: number, temp: any[]): void {
    let hourValueInMap = this.map.get(hour);
    if (hourValueInMap) {
      this.map.get(hour).push([temp[0], temp[1]])
    } else {
      this.map.set(hour, [[temp[0], temp[1]]]);
    }
  }

  hourIsActive(hour: number, color: string): any[] {
    let hourInMap = this.map.get(hour);
    if (hourInMap) {
      for (let i = 0; i < hourInMap.length; i++) {
        if (hourInMap[i][1] === color) {
          console.log(hour, hourInMap[i]);
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
          console.log(text);
          return [true, text];
        }
      }
    }
    return [false, 0];
  }

  tempLog(a: any): void {
    console.log(a);
  }
}
