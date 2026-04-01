import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
//todo: complete missing code..
private token:string|null = null;
private isLoggedIn:boolean=false;

constructor(){}

setId(id:any){
  localStorage.setItem('id',id)
}

get getId():string |null{
  return localStorage.getItem('id');
}

saveToken(token:string){
  localStorage.setItem('token',token);
}

setRole(role:any){
  localStorage.setItem('role',role);
}

get getRole():string|null{
  return localStorage.getItem('role');
}

get getLoginStatus():boolean{
  return !!localStorage.getItem('token');
}

getToken():string |null{
  this.token = localStorage.getItem('token');
  return this.token;
}

logout(){

  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('id');
  this.token=null;
  this.isLoggedIn = false;
}
}
