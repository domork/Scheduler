import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MyProfileComponent} from "./page/about/my-profile.component";
import {SchedulerComponent} from "./page/scheduler/scheduler.component";
import {LoginComponent} from "./forms/login/login.component";
import {SignupComponent} from "./forms/signup/signup.component";
import {CreateGroupComponent} from "./forms/create-group/create-group.component";
import {MyGroupsComponent} from "./page/my-groups/my-groups.component";
import {GroupDetailComponent} from "./page/group-detail/group-detail.component";
import {GroupPreferencesComponent} from "./utils/group-preferences/group-preferences.component";
import {JoinTemplateComponent} from "./forms/join-template/join-template.component";

const routes: Routes = [
  {path:'', component: SchedulerComponent},
  {path:'about', component: MyProfileComponent},
  {path:'login', component: LoginComponent},
  {path:'signup', component: SignupComponent},
  {path:'groups', component: MyGroupsComponent},
  {path:'groups/create', component:CreateGroupComponent},
  {path:'groups/join',component:JoinTemplateComponent},
  {path:'groups/:id', component: GroupDetailComponent},
  {path:'groups/:id/preferences', component: GroupPreferencesComponent},


];

@NgModule({
  imports: [RouterModule.forRoot(routes,{useHash:true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
