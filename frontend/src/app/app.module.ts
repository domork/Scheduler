import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HttpClientModule} from "@angular/common/http";
import { MyProfileComponent } from './page/about/my-profile.component';
import { SchedulerComponent } from './page/scheduler/scheduler.component';
import { httpInterceptorProviders } from './auth/auth-interceptor';
import { LoginComponent } from './page/login/login.component';
import {FormsModule} from "@angular/forms";
import { NavBarComponent } from './utils/nav-bar/nav-bar.component';
import { CreateTemplateComponent } from './utils/create-template/create-template.component';
import { NotificationHubComponent } from './utils/notification-hub/notification-hub.component';
import { SignupComponent } from './page/signup/signup.component';


@NgModule({
  declarations: [
    AppComponent,
    MyProfileComponent,
    SchedulerComponent,
    LoginComponent,
    NavBarComponent,
    CreateTemplateComponent,
    NotificationHubComponent,
    SignupComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [httpInterceptorProviders,NotificationHubComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
