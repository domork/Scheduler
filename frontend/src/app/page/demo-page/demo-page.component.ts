import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {TokenStorageService} from "../../auth/token-storage.service";
import {Router} from "@angular/router";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-demo-page',
  templateUrl: './demo-page.component.html',
  styleUrls: ['./demo-page.component.scss']
})
export class DemoPageComponent implements OnInit {



  constructor(private authService: AuthService,
              private tokenStorage: TokenStorageService,
              private router: Router,
              private notification: NotificationService) {
  }
  ngOnInit(): void {
  }

  onSubmit(): void {


    this.authService.demo().subscribe(
      data => {
        this.tokenStorage.signOut();
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveAuthorities(data.authorities);
        this.tokenStorage.saveUsername(data.username);

        this.router.navigate(['/']);
      }, error => {
        this.notification.sendError(error);
      }
    );
  }

}
