import {Component, OnInit} from '@angular/core';
import {Group} from "../../utils/dto/group";
import {GroupService} from "../../service/group.service";
import {TokenStorageService} from "../../auth/token-storage.service";
import {MatDialog} from '@angular/material/dialog';
import {Router} from "@angular/router";
import {GroupMember} from "../../utils/dto/group-member";

@Component({
  selector: 'app-my-groups',
  templateUrl: './my-groups.component.html',
  styleUrls: ['./my-groups.component.scss']
})
export class MyGroupsComponent implements OnInit {
  isLoggedIn = false;

  myGroups: any[] = [];
  selectedGroupMember: GroupMember | undefined;
  selectedGroupName: string = '';

  constructor(
    private groupService: GroupService,
    private tokenStorage: TokenStorageService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.getAllGroups();
    this.checkIfIsLoggedIn();
  }

  checkIfIsLoggedIn(): void {
    this.isLoggedIn = !!this.tokenStorage.getToken();

  }

  getAllGroups(): void {
    this.groupService.getAllGroups().subscribe(groups => {
      this.myGroups = groups;
      if (groups) {
        this.myGroups.forEach(group => {
          this.groupService.getGroupParticipantsForDay(new Date(group.time_to_start), group.id).subscribe(
            fetchedData => {
              let setOfAllUsers = new Set();
              let setOfActiveUsers = new Set();

              fetchedData.forEach(i => {
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
    }, err => {

      console.log(err);
    })
  }

  parseTime(group: Group): string {
    if (group.time_to_start) {
      let ret = '';
      let t = new Date(group.time_to_start);
      ret += t.getHours() + ":" + (t.getMinutes() < 10 ? '0' + t.getMinutes() : t.getMinutes());
      ret += ' on ' + t.getDate() + '.';
      ret += t.getMonth() + 1;
      return ret;
    }
    return '';
  }

  onGroupPreferencesClicked(group: any): void {
    this.groupService.getMemberInfoByUUID(group.userUUID).subscribe(data => {
      this.selectedGroupMember = data;
      this.selectedGroupMember.group_user_UUID = group.userUUID;
    }, error => console.log(error));
    this.selectedGroupName = group.name;
  }

  resetSelectedData(): void {
    this.selectedGroupName = '';
    this.selectedGroupMember = undefined;
  }

  leaveGroup(groupID: number): void {
    this.groupService.leaveGroup(groupID).subscribe(data => {
      if (data) {
        this.resetSelectedData();
        this.getAllGroups();

      }
    }, error => console.log(error));

  }

  editMemberInfo(data: any): void {
    this.groupService.updateMemberInfoByUUID(data.group_user_UUID, data.name, data.color).subscribe(data => {
      this.resetSelectedData();
      this.getAllGroups();
    }, error => {
    })
  }
}
