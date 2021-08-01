export class JwtResponse {
  accessToken: string;
  type: string;
  username: string;
  authorities: string[];

  constructor(accessToken: string, type: string, username: string, authorities: string[]) {
    this.accessToken = accessToken;
    this.type = type;
    this.username = username;
    this.authorities = authorities;
  }

}
