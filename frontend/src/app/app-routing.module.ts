import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MyProfileComponent} from "./page/my-profile/my-profile.component";
import {SchedulerComponent} from "./page/scheduler/scheduler.component";

const routes: Routes = [
  {path: '', component: MyProfileComponent},
  {path:'scheduler', component: SchedulerComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
