export class CreateGroupForm {
  name: string;
  password: string;
  description:string;

  constructor(name: string, password: string, description:string) {
    this.name = name;
    this.password = password;
    this.description=description;
  }
}
