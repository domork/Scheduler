import {Component, OnInit} from '@angular/core';
import {AuthLoginInfo} from "../../utils/dto/auth-form";
import {AuthService} from "../../service/auth.service";
import {CreateGroupForm} from "../../utils/dto/create-group-form";
import {GroupService} from "../../service/group.service";
import {Group} from "../../utils/dto/group";

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
    new Date(), '')

  constructor(private groupService: GroupService) {
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
      }, error => {
        /*defaultServiceErrorHandling(error);
       */
      }
    );

  }
}
