import {Injectable} from '@angular/core';
import {NotificationHubComponent} from "../utils/notification-hub/notification-hub.component";

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  notification: NotificationHubComponent | undefined;

  constructor() {
  }

  sendError(err: any) {
      this.notification?.defaultServiceErrorHandling(err);
  }

  sendSuccess(msg: any) {
    if (this.notification)
      this.notification.defaultServiceSuccessHandling(msg);
  }

  vanishAllNotifications():void{
    this.notification?.vanishSuccess();
    this.notification?.vanishError();
  }
}
