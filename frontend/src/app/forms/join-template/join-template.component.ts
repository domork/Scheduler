import {Component, OnInit} from '@angular/core';

import {GroupService} from "../../service/group.service";
import {JoinGroupForm} from "../../utils/dto/join-group-form";
import {Router} from "@angular/router";

@Component({
  selector: 'app-join-template',
  templateUrl: './join-template.component.html',
  styleUrls: ['./join-template.component.scss']
})
export class JoinTemplateComponent implements OnInit {

  form: any = {};
  joinGroupForm: JoinGroupForm = new JoinGroupForm(
    '', '', '');


  constructor(private router: Router, private groupService: GroupService) {
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
        this.router.navigate(['/groups']);
      }, error => {
        console.log(error);
      }
    );

  }
}
