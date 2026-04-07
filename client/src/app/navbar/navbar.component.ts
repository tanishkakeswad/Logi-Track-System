import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';

declare const bootstrap: any;

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  isLoggedIn = false;
  role: string | null = null;

  @ViewChild('navCollapse') navCollapse!: ElementRef;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // ✅ Set initial state immediately (so no refresh required)
    this.isLoggedIn = this.authService.getLoginStatus;
    this.role = this.authService.getRole;

    // ✅ React to login changes
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.role = status ? this.authService.getRole : null;
    });

    // ✅ Close hamburger after navigation
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => this.closeMenu());
  }

  closeMenu(): void {
    if (!this.navCollapse?.nativeElement) return;

    const el = this.navCollapse.nativeElement;
    if (el.classList.contains('show')) {
      const collapse = bootstrap.Collapse.getOrCreateInstance(el);
      collapse.hide();
    }
  }

  logout(): void {
    this.authService.logout();
    this.role = null;
    this.router.navigate(['/login']);
  }
}
