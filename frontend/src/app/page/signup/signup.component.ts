import { Component, OnInit } from '@angular/core';
import {AuthLoginInfo} from "../../utils/dto/auth-form";
import {AuthService} from "../service/auth.service";

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

  constructor(private authService: AuthService) {
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
      }, error => {
        /*defaultServiceErrorHandling(error);
        this.errorMessage = error.error.message;
       */
       this.isSignUpFailed = true;

      }
    );

  }}
}
