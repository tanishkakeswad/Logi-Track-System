import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  public serverName = environment.apiUrl;

  constructor(
  private http: HttpClient,
  private authService: AuthService
) {}

  // ✅ Common headers (REQUIRED for tests)
  private getHeaders(): HttpHeaders {
  const token = this.authService.getToken();

  let headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }

  return headers;
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
  



  downloadDocument(documentId: number) {
  return this.http.get(
    `${this.serverName}/api/documents/${documentId}/download`,
    {
      headers: this.getHeaders(),
      responseType: 'blob'
    }
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
  addCargoWithDocuments(formData: FormData): Observable<any> {
  const token = this.authService.getToken();
  let headers = new HttpHeaders();

  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }

  return this.http.post<any>(
    `${this.serverName}/api/business/cargo-with-documents`,
    formData,
    { headers }
  );
}
  // ================= AUTH =================
  Login(detail: any): Observable<any> {
  return this.http.post<any>(
    `${this.serverName}/api/login`,
    detail,
    { headers: new HttpHeaders({ 'Content-Type': 'application/json' }) }
  );
}

  registerUser(details: any): Observable<any> {
  return this.http.post<any>(
    `${this.serverName}/api/register`,
    details,
    { headers: new HttpHeaders({ 'Content-Type': 'application/json' }) }
  );
}

  sendOtp(email:string){
    return this.http.post(`${this.serverName}/api/send-otp?email=${email}`,{})
  }

  sendChatMessage(payload: any): Observable<any> {
  return this.http.post<any>(
    `${this.serverName}/api/chat/message`,
    payload,
    { headers: this.getHeaders() }
  );
}
}