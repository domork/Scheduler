import {Component, OnInit} from '@angular/core';
import {AuthLoginInfo} from "../../utils/dto/auth-form";
import {AuthService} from "../../service/auth.service";
import {TokenStorageService} from "../../auth/token-storage.service";
import {AppComponent} from "../../app.component";
import {Router} from "@angular/router";
import {NotificationService} from "../../service/notification.service";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  form: any = {};
  errorMessage = '';

  isLoggedIn = false;
  private loginInfo: AuthLoginInfo = new AuthLoginInfo(
    '', '');

  constructor(private authService: AuthService,
              private tokenStorage: TokenStorageService,
              private appComp: AppComponent,
              private router: Router,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.notification.vanishAllNotifications();
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    }
  }

  onSubmit(): void {

    this.loginInfo = new AuthLoginInfo(
      this.form.username, this.form.password
    );
    this.authService.attemptAuth(this.loginInfo).subscribe(
      data => {
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveAuthorities(data.authorities);
        this.tokenStorage.saveUsername(data.username);

        this.isLoggedIn = true;
        this.router.navigate(['/']);
      }, error => {
        this.errorMessage = error.error.message;
        this.notification.sendError(error);
      }
    );
  }


}
