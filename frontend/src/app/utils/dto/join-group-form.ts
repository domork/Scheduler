export class JoinGroupForm {
  name: string;
  password: string;
  userName: string;

  constructor(name: string, password: string, userName: string) {
    this.name = name;
    this.password = password;
    this.userName = userName;
  }
}
