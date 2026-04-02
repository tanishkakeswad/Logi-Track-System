
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private token: string | null = null;

  // ✅ Login state observable
  private loggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$ = this.loggedInSubject.asObservable();

  constructor() {}

  setId(id: any) {
    localStorage.setItem('id', id);
  }

  get getId(): string | null {
    return localStorage.getItem('id');
  }

  saveToken(token: string) {
    localStorage.setItem('token', token);
    this.token = token;

    // ✅ Notify app that user is logged in
    this.loggedInSubject.next(true);
  }

  setRole(role: any) {
    localStorage.setItem('role', role);
  }

  get getRole(): string | null {
    return localStorage.getItem('role');
  }

  get getLoginStatus(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    this.token = localStorage.getItem('token');
    return this.token;
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('id');
    this.token = null;

    // ✅ Notify app that user logged out
    this.loggedInSubject.next(false);
  }

  // ✅ Used on app reload
  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }
}
