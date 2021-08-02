import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-notification-hub',
  templateUrl: './notification-hub.component.html',
  styleUrls: ['./notification-hub.component.scss']
})
export class NotificationHubComponent implements OnInit {

  successMessage = '';
  errorMessage = '';


  constructor() {
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

    (async () => {
      if (error.status === 0) {
        // If status is 0, the backend is probably down
        this.errorMessage = 'The backend seems not to be reachable';
      } else if (error.status === 401) {
        this.errorMessage = 'Login or password is incorrect!';
      } else if (error.error.message === 'No message available') {
        // If no detailed error message is provided, fall back to the simple error name
        this.errorMessage = error.error.error;
      } else {
        this.errorMessage = error.error.message;
      }
      await this.delay(7000);
      this.vanishError();
    })();
  }

  public defaultServiceSuccessHandling(msg: string): void {
    (async () => {
      this.successMessage = msg;
      await this.delay(7000);
      this.vanishSuccess();
    })();
  }
}
