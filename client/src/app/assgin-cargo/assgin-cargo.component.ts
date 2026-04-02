import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-assgin-cargo',
  templateUrl: './assgin-cargo.component.html',
  styleUrls: ['./assgin-cargo.component.scss']
})
export class AssginCargoComponent implements OnInit {

  showError: boolean = false;
  errorMessage: string = '';
  cargList: any[] = [];
  statusModel: any = {};
  showMessage: boolean = false;
  responseMessage: string = '';
  id1: number = 0;

  constructor(
    private router: Router,
    private httpService: HttpService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // ✅ Get Driver ID correctly
    const id = this.authService.getId;

    if (!id) {
      console.error("Driver ID not found");
      this.showError = true;
      this.errorMessage = "User not logged in properly";
      return;
    }

    this.id1 = Number(id);

    console.log("Driver ID:", this.id1);

    this.getAssignedCargo();
    this.statusModel.newStatus = null;
  }

  // ✅ Fetch assigned cargo
  getAssignedCargo() {
    this.cargList = [];

    this.httpService.getAssignOrders(this.id1).subscribe({
      next: (data: any) => {
        console.log("Cargo Data:", data);

        // handle null or empty
        if (data && data.length > 0) {
          this.cargList = data;
        } else {
          this.cargList = [];
          this.showError = true;
          this.errorMessage = "No cargo assigned yet";
        }
      },
      error: (error) => {
        console.error("API Error:", error);
        this.showError = true;
        this.errorMessage = "Error fetching cargo data";
      }
    });
  }

  // ✅ Set selected cargo
  addStatus(cargo: any) {
    this.statusModel.cargoId = cargo.id;
    this.statusModel.status = cargo.status;
  }

  // ✅ Update cargo status
  assignDriver() {
    if (!this.statusModel.newStatus) {
      return;
    }

    this.showMessage = false;
    this.showError = false;

    this.httpService.updateCargoStatus(
      this.statusModel.newStatus,
      this.statusModel.cargoId
    ).subscribe({
      next: (data: any) => {
        this.showMessage = true;
        this.responseMessage = data.message || "Status updated successfully";
        this.getAssignedCargo(); // refresh table
      },
      error: (error) => {
        console.error(error);
        this.showError = true;
        this.errorMessage = "Error updating status";
      }
    });
  }

  // ✅ Logout
  // logout() {
  //   this.authService.logout();
  //   this.router.navigate(['/login']);
  // }
}