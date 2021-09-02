import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import {AuthLoginInfo} from '../utils/dto/auth-form';
import {environment}   from '../../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

const baseUri = environment.backendUrl + '/';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loginUrl = baseUri + 'signin';
  private signupUrl = baseUri + 'signup';

  constructor(private http: HttpClient) {
  }

  attemptAuth(credentials: AuthLoginInfo): Observable<any> {
    return this.http.post(this.loginUrl, credentials, httpOptions);
  }

  signUp(info: AuthLoginInfo): Observable<any> {
    return this.http.post(this.signupUrl, info, httpOptions);
  }

  demo():Observable<any>{
    return this.http.post(baseUri+'demo',null);
  }
}
