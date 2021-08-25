import { Component, OnInit } from '@angular/core';
import {TokenStorageService} from "../../auth/token-storage.service";

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.scss']
})
export class SchedulerComponent implements OnInit {
  isLoggedIn = false;

  constructor(private tokenStorage:TokenStorageService) { }

  ngOnInit(): void {
    this.checkIfIsLoggedIn();
  }
  checkIfIsLoggedIn(): void {
    this.isLoggedIn = !!this.tokenStorage.getToken();

  }
}
