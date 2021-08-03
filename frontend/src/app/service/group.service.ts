import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Group} from "../utils/dto/group";
import {CreateGroupForm} from "../utils/dto/create-group-form";

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
}
