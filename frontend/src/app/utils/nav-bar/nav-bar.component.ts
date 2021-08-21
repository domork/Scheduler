import {Component, Inject, OnInit, Renderer2} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import {TokenStorageService} from "../../auth/token-storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {
  menuIsActive:boolean = false;
  constructor(
    private _renderer2: Renderer2,
    @Inject(DOCUMENT) private _document: Document,
    private tokenStorage: TokenStorageService,
    private router: Router) {
  }

  isLoggedIn(): boolean {
    return !!this.tokenStorage.getToken();

  }

  public ngOnInit() {


  }

  changeLocation(): void {

  }

  logout(): void {
    if (this.tokenStorage.getUsername()) {
      this.tokenStorage.signOut();
    }
  }
  toggleMenu():void{
    this.menuIsActive= !this.menuIsActive;
  }
}
