import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Group} from "../utils/dto/group";
import {CreateGroupForm} from "../utils/dto/create-group-form";
import {GroupMember} from "../utils/dto/group-member";
import {TimeIntervalByUser} from "../utils/dto/time-interval-by-user";
import {JoinGroupForm} from "../utils/dto/join-group-form";

const baseUri = environment.backendUrl + '/groups';


@Injectable({
  providedIn: 'root'
})
export class GroupService {
  httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'})};

  constructor(private http: HttpClient) {
  }

  getAllGroups(): Observable<Group[]>{
    return this.http.get<Group[]>(baseUri);
  }

  addGroup(group:CreateGroupForm): Observable<Group>{
    return this.http.post<Group>(baseUri,group,this.httpOptions);
  }

  getGroupParticipantsForDay(date: Date, groupID: number): Observable<GroupMember[]>{
    return this.http.get<GroupMember[]>(baseUri+'/'+groupID+'?date='+date.toISOString());
  }

  addNewInterval(groupID: number, timeInterval:TimeIntervalByUser): Observable<TimeIntervalByUser>{
    return this.http.post<TimeIntervalByUser>(baseUri+'/'+groupID, timeInterval,this.httpOptions);
  }

  joinGroup(credentials: JoinGroupForm):Observable<JoinGroupForm>{
    return this.http.post<JoinGroupForm>(baseUri+'/join', credentials,this.httpOptions);
  }
}
