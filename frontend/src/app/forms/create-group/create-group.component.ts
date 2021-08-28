import {Component, OnInit} from '@angular/core';
import {CreateGroupForm} from "../../utils/dto/create-group-form";
import {GroupService} from "../../service/group.service";
import {Group} from "../../utils/dto/group";
import {Router} from "@angular/router";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-create-group',
  templateUrl: './create-group.component.html',
  styleUrls: ['./create-group.component.scss']
})
export class CreateGroupComponent implements OnInit {

  form: any = {};
  createGroupForm: CreateGroupForm = new CreateGroupForm(
    '', '', '');
  fetchedGroup: Group = new Group(0, '', '',
    new Date(), '','', undefined,undefined,undefined)

  constructor(private groupService: GroupService,
              private router: Router,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.createGroupForm = new CreateGroupForm(
      this.form.name,
      this.form.password,
      this.form.description
    );
    this.groupService.addGroup(this.createGroupForm).subscribe(
      data => {
        this.fetchedGroup = data;
        this.notification.sendSuccess('Successfully created a group')
        this.router.navigate(['/groups']);

      }, error => {
        this.notification.sendError(error);
      }
    );

  }
}
