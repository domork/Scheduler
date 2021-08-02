import {Component, Inject, OnInit} from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.scss']
})
export class MyProfileComponent implements OnInit {

  constructor(@Inject(DOCUMENT) public document: Document) { }

  ngOnInit(): void {
  }

  goToGithubUrl(): void {
    this.document.location.href = 'https://github.com/domork/Scheduler';
  }

}
