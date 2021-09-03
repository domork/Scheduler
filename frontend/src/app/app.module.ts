import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HttpClientModule} from "@angular/common/http";
import { MyProfileComponent } from './page/about/my-profile.component';
import { SchedulerComponent } from './page/scheduler/scheduler.component';
import { httpInterceptorProviders } from './auth/auth-interceptor';
import { LoginComponent } from './forms/login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { NavBarComponent } from './utils/nav-bar/nav-bar.component';
import { NotificationHubComponent } from './utils/notification-hub/notification-hub.component';
import { SignupComponent } from './forms/signup/signup.component';
import { CreateGroupComponent } from './forms/create-group/create-group.component';
import { MyGroupsComponent } from './page/my-groups/my-groups.component';
import { GroupDetailComponent } from './page/group-detail/group-detail.component';
import { GroupPreferencesComponent } from './utils/group-preferences/group-preferences.component';
import { JoinTemplateComponent } from './forms/join-template/join-template.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatDialogModule} from '@angular/material/dialog';
import { ColorSketchModule } from 'ngx-color/sketch';
import {ColorChromeModule} from "ngx-color/chrome";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {MatInputModule} from "@angular/material/input";
import {NgxMatTimepickerModule} from "ngx-mat-timepicker";
import {MatIconModule} from "@angular/material/icon";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import { DemoPageComponent } from './page/demo-page/demo-page.component';
import { IssueReporterComponent } from './page/issue-reporter/issue-reporter.component';

@NgModule({
  declarations: [
    AppComponent,
    MyProfileComponent,
    SchedulerComponent,
    LoginComponent,
    NavBarComponent,
    NotificationHubComponent,
    SignupComponent,
    CreateGroupComponent,
    MyGroupsComponent,
    GroupDetailComponent,
    GroupPreferencesComponent,
    JoinTemplateComponent,
    DemoPageComponent,
    IssueReporterComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    ColorSketchModule,
    MatDialogModule,
    ColorChromeModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    NgxMatTimepickerModule.setLocale('en-GB'),
    MatIconModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
