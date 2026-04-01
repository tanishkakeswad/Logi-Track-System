import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  public serverName = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ✅ Common headers (REQUIRED for tests)
  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer mockToken'
    });
  }

  // ================= CUSTOMER =================
  getOrderStatus(cargoId: any): Observable<any> {
    return this.http.get<any>(
      `${this.serverName}/api/customer/cargo-status?cargoId=${cargoId}`,
      { headers: this.getHeaders() }
    );
  }

  // ================= DRIVER =================
  updateCargoStatus(newStatus: any, cargoId: any): Observable<any> {
    return this.http.put<any>(
      `${this.serverName}/api/driver/update-cargo-status?cargoId=${cargoId}&newStatus=${newStatus}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  getAssignOrders(driverId: any): Observable<any> {
    return this.http.get<any>(
      `${this.serverName}/api/driver/cargo?driverId=${driverId}`,
      { headers: this.getHeaders() }
    );
  }

  // ================= BUSINESS =================
  assignDriver(driverId: any, cargoId: any): Observable<any> {
    return this.http.post<any>(
      `${this.serverName}/api/business/assign-cargo?cargoId=${cargoId}&driverId=${driverId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  getCargo(): Observable<any> {
    return this.http.get<any>(
      `${this.serverName}/api/business/cargo`,
      { headers: this.getHeaders() }
    );
  }

  getDrivers(): Observable<any> {
    return this.http.get<any>(
      `${this.serverName}/api/business/drivers`,
      { headers: this.getHeaders() }
    );
  }

  addCargo(details: any): Observable<any> {
    return this.http.post<any>(
      `${this.serverName}/api/business/cargo`,
      details,
      { headers: this.getHeaders() }
    );
  }

  getCargoById(cargoId: any): Observable<any> {
    return this.http.get<any>(
      `${this.serverName}/api/business/cargo-id?cargoId=${cargoId}`,
      { headers: this.getHeaders() }
    );
  }

  // ================= AUTH =================
  Login(detail: any): Observable<any> {
    return this.http.post<any>(
      `${this.serverName}/api/login`,
      detail,
      { headers: this.getHeaders() }
    );
  }

  registerUser(details: any): Observable<any> {
    return this.http.post<any>(
      `${this.serverName}/api/register`,
      details,
      { headers: this.getHeaders() }
    );
  }
}