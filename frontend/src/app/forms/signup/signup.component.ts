import { Component, OnInit } from '@angular/core';
import {AuthLoginInfo} from "../../utils/dto/auth-form";
import {AuthService} from "../../service/auth.service";
import {Router} from "@angular/router";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  form: any = {};
  signupInfo: AuthLoginInfo = new AuthLoginInfo(
    '', '');
  isSignedUp = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private router: Router,
              private authService: AuthService,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.signupInfo = new AuthLoginInfo(
      this.form.name,
      this.form.password
    );
    if (this.form.repeated_password==this.form.password){
    this.authService.signUp(this.signupInfo).subscribe(
      data => {
        this.isSignedUp = true;
        this.isSignUpFailed = false;
        this.notification.sendSuccess('Successfully registered! Please log in.')
        this.router.navigate(['/groups']);
      }, error => {
       this.isSignUpFailed = true;
        this.notification.sendError(error);
      }
    );

  }}
}
