import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';


@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {

  const role = this.authService.getRole?.trim().toUpperCase();
  const path = route.routeConfig?.path;

  if (!this.authService.getLoginStatus) {
    this.router.navigate(['/login']);
    return false;
  }

  if (path?.includes('business') && role === 'BUSINESS') return true;
  if (path?.includes('driver') && role === 'DRIVER') return true;
  if (path?.includes('customer') && role === 'CUSTOMER') return true;

  // Logged in but wrong role
  this.router.navigate(['/login']);
  return false;
}
}