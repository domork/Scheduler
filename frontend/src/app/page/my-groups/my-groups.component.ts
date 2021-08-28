import {Component, OnInit} from '@angular/core';
import {Group} from "../../utils/dto/group";
import {GroupService} from "../../service/group.service";
import {TokenStorageService} from "../../auth/token-storage.service";
import {Router} from "@angular/router";
import {GroupMember} from "../../utils/dto/group-member";
import {NotificationService} from "../../service/notification.service";

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
  fetchingGroups: boolean = false;

  constructor(
    private groupService: GroupService,
    private tokenStorage: TokenStorageService,
    private router: Router,
    private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.checkIfIsLoggedIn();
    this.getAllGroups();
  }

  checkIfIsLoggedIn(): void {
    this.isLoggedIn = !!this.tokenStorage.getToken();

  }

  getAllGroups(): void {
    if (!this.isLoggedIn)
      return;
    this.fetchingGroups=true;
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
              this.fetchingGroups=false;
            }
          );
        })
      }
    }, err => {
      if (err.status === 404) {
        if (err.error.message)
          err.error.message = 'There are no groups yet. Create or join one ;)'
        this.notification.sendError(err);
      } else
        this.notification.sendError(err);
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
    }, error => this.notification.sendError(error));
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
    }, error => this.notification.sendError(error));

  }

  editMemberInfo(data: any): void {
    this.groupService.updateMemberInfoByUUID(data.group_user_UUID, data.name, data.color).subscribe(data => {
      this.resetSelectedData();
      this.getAllGroups();
    }, error => {
      this.notification.sendError(error);
    })
  }
}
