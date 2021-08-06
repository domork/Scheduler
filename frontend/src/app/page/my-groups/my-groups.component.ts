import {Component, OnInit} from '@angular/core';
import {Group} from "../../utils/dto/group";
import {GroupService} from "../../service/group.service";

@Component({
  selector: 'app-my-groups',
  templateUrl: './my-groups.component.html',
  styleUrls: ['./my-groups.component.scss']
})
export class MyGroupsComponent implements OnInit {
  myGroups: Group[] = [new Group(0, '', '', new Date(), '')];

  constructor(private groupService: GroupService) {
  }

  ngOnInit(): void {
     this.getAllGroups();
  }

  getAllGroups(): void {
    this.groupService.getAllGroups().subscribe(groups => {
      this.myGroups = groups;
      console.log(this.myGroups);
    }, err => {

    })
  }

  parseTime(group:Group): string {
    let t = group.time_to_start.toLocaleString();
    return t.substring(10, 15) + ' | ' + t.substring(0, 3);
  }
}
