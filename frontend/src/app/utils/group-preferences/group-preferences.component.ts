import {Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import {GroupService} from "../../service/group.service";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {Router} from "@angular/router";

@Component({
  selector: 'app-group-preferences',
  templateUrl: './group-preferences.component.html',
  styleUrls: ['./group-preferences.component.scss']
})
export class GroupPreferencesComponent implements OnInit {

  @Input() groupID: number=-1;
  @Output() quitGroup: EventEmitter<any> = new EventEmitter<any>();
  constructor(private service: GroupService,
              @Inject(MAT_DIALOG_DATA) public data: number,
              private router: Router) { }

  ngOnInit(): void {
  }

  onLeaveButtonClicked():void{
    this.service.leaveGroup(this.data).subscribe(data=>{

      if (data)
        this.router.navigate(['/']);
    },error => console.log(error));

  }


}
