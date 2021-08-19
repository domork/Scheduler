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
      console.log(this.myGroups);
    }, err => {

      console.log(err);
    })
  }

  parseTime(group: Group): string {
    if (group.time_to_start) {
      let ret ='';
      let t = new Date(group.time_to_start);

      ret+=t.getHours()+":"+t.getMinutes();
      ret+=' on '+t.getDate() + '.';
      ret+=t.getMonth();
      return ret;
    }
    return '';
  }

  onGroupPreferencesClicked(groupID:number):void{
    const dialogRef = this.dialog.open(GroupPreferencesComponent, {
      width: '580px',
      data: groupID
    });

  }
}
