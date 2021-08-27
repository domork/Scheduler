import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Group} from "../utils/dto/group";
import {CreateGroupForm} from "../utils/dto/create-group-form";
import {TimeIntervalByUser} from "../utils/dto/time-interval-by-user";
import {JoinGroupForm} from "../utils/dto/join-group-form";
import {GroupMember} from "../utils/dto/group-member";

const baseUri = environment.backendUrl + '/groups';


@Injectable({
  providedIn: 'root'
})
export class GroupService {
  httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'})};

  constructor(private http: HttpClient) {
  }

  getAllGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(baseUri);
  }

  getMemberInfoByUUID(UUID: string): Observable<GroupMember> {
    return this.http.get<GroupMember>(baseUri + '/' + UUID + '/memberInfo');
  }

  updateMemberInfoByUUID(UUID: string, name: string, color: string): Observable<any> {
    //These params don't work. Need to check later.
    /*let params = new HttpParams().set('color',color).set('name', name);
    const opt = {headers: new HttpHeaders({'Content-Type': 'application/json'}), params};
     */
    return this.http.post<GroupMember>
    (baseUri + '/' + UUID + '/memberInfo?color=' + encodeURIComponent(color) + '&name=' + encodeURIComponent(name), this.httpOptions);

  }

  addGroup(group: CreateGroupForm): Observable<Group> {
    return this.http.post<Group>(baseUri, group, this.httpOptions);
  }

  getGroupParticipantsForDay(date: Date, groupID: number): Observable<TimeIntervalByUser[]> {
    return this.http.get<TimeIntervalByUser[]>(baseUri + '/' + groupID + '?date=' + date.toISOString());
  }

  addNewInterval(groupID: number, timeInterval: TimeIntervalByUser): Observable<TimeIntervalByUser> {
    return this.http.post<TimeIntervalByUser>(baseUri + '/' + groupID, timeInterval, this.httpOptions);
  }

  joinGroup(credentials: JoinGroupForm): Observable<JoinGroupForm> {
    return this.http.post<JoinGroupForm>(baseUri + '/join', credentials, this.httpOptions);
  }

  leaveGroup(id: number): Observable<Boolean> {
    return this.http.post<Boolean>(baseUri + '/' + id + '/leave', this.httpOptions);
  }
  deleteInterval(uuid:string,date: Date): Observable<boolean>{
   // date.setHours(date.getHours()-date.getTimezoneOffset()/60)
    let params = new HttpParams().set('date',date.toISOString()).set('UUID', uuid);
    const opt = {headers: new HttpHeaders({'Content-Type': 'application/json'}), params};
    return this.http.delete<boolean>(baseUri + '/', opt);
  }
}
