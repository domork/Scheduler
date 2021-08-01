import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import {JwtResponse} from '../../dto/jwt-response';
import {AuthLoginInfo} from '../../dto/login-info';
import {SignupInfo}    from '../../dto/signup-info';
import {environment}   from '../../../environments/environment';

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

  signUp(info: SignupInfo): Observable<any> {
    return this.http.post(this.signupUrl, info, httpOptions);
  }
}
