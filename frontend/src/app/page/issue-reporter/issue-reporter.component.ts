import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {TokenStorageService} from "../../auth/token-storage.service";
import {Router} from "@angular/router";
import {NotificationService} from "../../service/notification.service";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-issue-reporter',
  templateUrl: './issue-reporter.component.html',
  styleUrls: ['./issue-reporter.component.scss']
})
export class IssueReporterComponent implements OnInit {

  message:string ='';
  form: FormGroup = this.fb.group({
    s: [null]
  });

  constructor(private authService: AuthService,
              private tokenStorage: TokenStorageService,
              private router: Router,
              private notification: NotificationService,
              private fb: FormBuilder
  ) {}

  ngOnInit(): void {
  }

  onSubmit(): void {
     this.authService.report(JSON.stringify(this.form.value.s)).subscribe(
       data => {
         this.notification.sendSuccess('Your message has been successfully sent. Thank you! ')
         this.router.navigate(['/']);
       }, error => {
         this.notification.sendError(error);
       }
     );
   }
}
