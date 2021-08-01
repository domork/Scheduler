import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HttpClientModule} from "@angular/common/http";
import { MyProfileComponent } from './page/my-profile/my-profile.component';
import { SchedulerComponent } from './page/scheduler/scheduler.component';
import { httpInterceptorProviders } from './auth/auth-interceptor';
import { LoginComponent } from './page/login/login.component';
import {FormsModule} from "@angular/forms";


@NgModule({
  declarations: [
    AppComponent,
    MyProfileComponent,
    SchedulerComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
