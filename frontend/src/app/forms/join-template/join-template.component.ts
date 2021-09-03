import {Component, OnInit} from '@angular/core';

import {GroupService} from "../../service/group.service";
import {JoinGroupForm} from "../../utils/dto/join-group-form";
import {Router} from "@angular/router";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-join-template',
  templateUrl: './join-template.component.html',
  styleUrls: ['./join-template.component.scss']
})
export class JoinTemplateComponent implements OnInit {

  form: any = {};
  joinGroupForm: JoinGroupForm = new JoinGroupForm(
    '', '', '');


  constructor(private router: Router, private groupService: GroupService,
              private notification:NotificationService) {
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.joinGroupForm = new JoinGroupForm(
      this.form.name,
      this.form.password,
      this.form.userName
    );
    this.groupService.joinGroup(this.joinGroupForm).subscribe(
      data => {
        this.joinGroupForm = data;
        this.notification.sendSuccess('Successfully joined a group')
        this.router.navigate(['/groups']);
      }, error => {
        this.notification.sendError(error);

      }
    );

  }
}
