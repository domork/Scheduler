import { Component, OnInit} from '@angular/core';
import {NotificationService} from "../../service/notification.service";
import {interval} from "rxjs";
import {ThemePalette} from "@angular/material/core";
import {repeatGroups} from "@angular/compiler/src/shadow_css";



@Component({
  selector: 'app-notification-hub',
  templateUrl: './notification-hub.component.html',
  styleUrls: ['./notification-hub.component.scss']
})

export class NotificationHubComponent implements OnInit {

 successMessage = '';
 errorMessage = '';
 progressbarValue = 100;
 curSec: number = 0;
 color: ThemePalette = "warn"

  constructor(private service:NotificationService) {
    service.notification = this;
  }

  ngOnInit(): void {
  }

  vanishError(): void {
    this.errorMessage = '';
  }

  vanishSuccess(): void {
    this.successMessage = '';
  }

  public delay(ms: number): Promise<any> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  public defaultServiceErrorHandling(error: any): void {
    this.vanishSuccess();
    this.vanishError();
    (async () => {

      if (error.status === 0) {
        // If status is 0, the backend is probably down
        this.errorMessage = 'The backend seems not to be reachable';
      } else if (error.status === 401) {
        this.errorMessage = 'Login or password is incorrect!';
      } else if (error.error.message === 'No message available') {
        // If no detailed error message is provided, fall back to the simple error name
        this.errorMessage = error.error.error;
      }
      else {
        this.errorMessage = error.error.message;
      }
      this.color = "warn";
      this.startTimer(65);
      await this.delay(7000);
      this.vanishError();
    })()
  }

  public defaultServiceSuccessHandling(msg: string): void {
    this.vanishSuccess();
    this.vanishError();
    (async () => {
      this.successMessage = msg;
      this.color = "primary";
      this.startTimer(65);
      await this.delay(7000);
      this.vanishSuccess();
    })();
  }


  startTimer(seconds: number) {
    const time = seconds;
    const timer$ = interval(100);

    const sub = timer$.subscribe((sec) => {
      this.progressbarValue = 100 - sec * 100 / seconds;
      this.curSec = sec;

      if (this.curSec === seconds) {
        sub.unsubscribe();
      }
    });
  }

}
