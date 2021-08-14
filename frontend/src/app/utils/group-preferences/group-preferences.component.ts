import {Component, Input, OnInit} from '@angular/core';
import {GroupService} from "../../service/group.service";
import {group} from "@angular/animations";

@Component({
  selector: 'app-group-preferences',
  templateUrl: './group-preferences.component.html',
  styleUrls: ['./group-preferences.component.scss']
})
export class GroupPreferencesComponent implements OnInit {

  @Input() groupID: number=-1;
  constructor(private service: GroupService) { }

  ngOnInit(): void {
  }

  onLeaveButtonClicked():void{
    this.service.leaveGroup(this.groupID).subscribe(data=>{

    },error => console.log(error));
  }
}
