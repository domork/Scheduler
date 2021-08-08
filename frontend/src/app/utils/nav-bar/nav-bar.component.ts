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

  constructor(
    private _renderer2: Renderer2,
    @Inject(DOCUMENT) private _document: Document,
    private tokenStorage: TokenStorageService,
    private router: Router) {
  }
  isLoggedIn():boolean{
    return !!this.tokenStorage.getToken();

  }

  public ngOnInit() {

    let script = this._renderer2.createElement('script');
    script.type = `text/javascript`;
    script.text = `
          document.addEventListener('DOMContentLoaded', () => {

    // Get all "navbar-burger" elements
    const $navbarBurgers = Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0);

    // Check if there are any navbar burgers
    if ($navbarBurgers.length > 0) {

      // Add a click event on each of them
      $navbarBurgers.forEach( el => {
        el.addEventListener('click', () => {

          // Get the target from the "data-target" attribute
          const target = el.dataset.target;
          const $target = document.getElementById(target);

          // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
          el.classList.toggle('is-active');
          $target.classList.toggle('is-active');

        });
      });
    }

  });
        `;
    this._renderer2.appendChild(this._document.body, script);
  }

  logout(): void {
    if (this.tokenStorage.getUsername()) {
      this.tokenStorage.signOut();
    }
  }

}
