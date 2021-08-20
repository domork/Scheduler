import {Component, OnInit} from '@angular/core';
import {Group} from "../../utils/dto/group";
import {GroupService} from "../../service/group.service";
import {TokenStorageService} from "../../auth/token-storage.service";
import {MatDialog} from '@angular/material/dialog';
import {GroupPreferencesComponent} from "../../utils/group-preferences/group-preferences.component";

@Component({
  selector: 'app-my-groups',
  templateUrl: './my-groups.component.html',
  styleUrls: ['./my-groups.component.scss']
})
export class MyGroupsComponent implements OnInit {
  myGroups: any[] = [];

  constructor(
    private groupService: GroupService,
    private tokenStorage: TokenStorageService,
    public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.getAllGroups();
  }

  getAllGroups(): void {
    this.groupService.getAllGroups().subscribe(groups => {
      this.myGroups = groups;
      if (groups) {
        this.myGroups.forEach(group => {
          this.groupService.getGroupParticipantsForDay(new Date(group.time_to_start), group.id).subscribe(
            fetchedData =>{
              console.log(fetchedData);
              let setOfAllUsers = new Set();
              let setOfActiveUsers = new Set();

              fetchedData.forEach(i=>{
                setOfAllUsers.add(i.group_user_UUID);
                if (i.time_end)
                  setOfActiveUsers.add(i.group_user_UUID);
              })
              let indexOfGroup = this.myGroups.indexOf(group);
              this.myGroups[indexOfGroup].numOfAllPeople = setOfAllUsers.size;
              this.myGroups[indexOfGroup].numOfRdyPeople = setOfActiveUsers.size;
              this.myGroups[indexOfGroup].iPickedTime = setOfActiveUsers.has(group.userUUID);

            }
          );
        })
      }
      console.log(this.myGroups);
    }, err => {

      console.log(err);
    })
  }

  parseTime(group: Group): string {
    if (group.time_to_start) {
      let ret = '';
      let t = new Date(group.time_to_start);
      console.log(t, t.getMonth());
      ret += t.getHours() + ":" + (t.getMinutes()<10?'0'+t.getMinutes():t.getMinutes());
      ret += ' on ' + t.getDate() + '.';
      ret += t.getMonth()+1;
      return ret;
    }
    return '';
  }

  onGroupPreferencesClicked(groupID: number): void {
    const dialogRef = this.dialog.open(GroupPreferencesComponent, {
      width: '580px',
      data: groupID
    });

  }
}
