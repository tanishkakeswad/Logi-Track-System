import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment.development';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  //todo: complete missing code..
  public serverName = environment.apiUrl;

  constructor(private http:HttpClient){

  }

  //Customer Controller
  getOrderStatus(cargoId:any):Observable<any>{
    return this.http.get<any>(`${this.serverName}/api/customer/cargo-status/${cargoId}`);
  }
  
  //Driver controller
  updateCargoStatus(newStatus:any,cargoId:any):Observable<any>{
    return this.http.put<any>(`${this.serverName}/api/driver/update-cargo-status/${cargoId}`,newStatus);
  }

  //business controller
  assignDriver(driverid:any,cargoId:any):Observable<any>{
    return this.http.post<any>(`${this.serverName}/api/business/${cargoId}`,driverid);
  }

  //Driver controller
  getAssignOrders(driverId:any):Observable<any>{
    return this.http.get<any>(`${this.serverName}/api/driver/cargo/${driverId}`);
  }

  //business controller
  getCargo():Observable<any>{
    return this.http.get<any>(`${this.serverName}/api/business/cargo`);
  }

  //business controller
  getDrivers():Observable<any>{
     return this.http.get<any>(`${this.serverName}/api/business/drivers`);
  }

  //business controller
  addCargo(details:any):Observable<any>{
      return this.http.post<any>(`${this.serverName}/api/business/cargo`,details);
  }

  //method not present in the controller we have to make it 
  getCargoById(cargoId: any): Observable<any> {
    return this.http.get<any>(`${this.serverName}/api/business/cargo-id/${cargoId}`)
    // return this.http.get(this.serverName + `/api/business/cargo-id?cargoId=` + cargoId, { headers: headers });
  }

  //register and login controller
  Login(detail:any):Observable<any>{
    return this.http.post<any>(`${this.serverName}/login`,detail);
  }

  //register and login controller
  registerUser(details:any):Observable<any>{
    return this.http.post<any>(`${this.serverName}/register`,details);
  }
}
