import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

import {GroupMember} from "../dto/group-member";

@Component({
  selector: 'app-group-preferences',
  templateUrl: './group-preferences.component.html',
  styleUrls: ['./group-preferences.component.scss']
})
export class GroupPreferencesComponent implements OnInit {

  @Input() groupMember: GroupMember = new GroupMember(-1,-1,'','','');
  @Input() groupName: string = '';

  @Output() close: EventEmitter<any> = new EventEmitter<any>();
  @Output() quitGroup: EventEmitter<number> = new EventEmitter<number>();
  @Output() editInfo: EventEmitter<any>= new EventEmitter<any>();
  editButtonClicked: boolean = false;
  form: any = {};

  constructor() {
  }

  ngOnInit(): void {
    this.form.username=this.groupMember.name;
    this.form.color= this.groupMember.color;
    this.form.group_user_UUID=this.groupMember.group_user_UUID;
  }

  onLeaveButtonClicked(): void {
    this.quitGroup.emit(this.groupMember?.group_id);
  }

  closeModal(): void {
    this.close.emit();
  }

  onEditButtonClicked(): void {
    this.editButtonClicked = true;

  }
  onDiscardButtonClicked(): void {
    this.closeModal();
  }

  changeColor(c:any):void{
    this.form.color=c.color.hex;
  }
  onSubmit(): void {
    this.editInfo.emit({name:this.form.username,color:this.form.color, group_user_UUID:this.form.group_user_UUID});
    this.closeModal();
  }
}
