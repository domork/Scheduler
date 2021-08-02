import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MyProfileComponent} from "./page/about/my-profile.component";
import {SchedulerComponent} from "./page/scheduler/scheduler.component";
import {LoginComponent} from "./page/login/login.component";
import {SignupComponent} from "./page/signup/signup.component";

const routes: Routes = [
  {path:'', component: SchedulerComponent},
  {path: 'about', component: MyProfileComponent},
  {path:'login', component: LoginComponent},
  {path:'signup', component: SignupComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
