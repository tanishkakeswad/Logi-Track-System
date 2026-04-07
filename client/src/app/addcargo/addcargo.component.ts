import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';
import { AuthService } from '../../services/auth.service';

declare const bootstrap: any;

@Component({
  selector: 'app-addcargo',
  templateUrl: './addcargo.component.html',
  styleUrls: ['./addcargo.component.scss']
})
export class AddcargoComponent implements OnInit {

  itemForm: FormGroup;

  cargList: any[] = [];
  cargoToShow: any[] = [];

  driverList: any[] = [];
  driverId: number | null = null;

  cargoId: string = '';
  selectedFiles: File[] = [];

  addSuccess = false;
  addMessage = '';
  addError = false;
  addErrorMessage = '';

  assignSuccess = false;
  assignMessage = '';
  assignError = false;
  assignErrorMessage = '';

  showError = false;
  errorMessage = '';

  private assignedDriverMap: Record<number, number> = {};
  private readonly ASSIGN_MAP_KEY = 'assignedDriverMap';

  constructor(
    public router: Router,
    public httpService: HttpService,
    private formBuilder: FormBuilder,
    private authService: AuthService
  ) {
    this.itemForm = this.formBuilder.group({
      content: ['', Validators.required],
      size: ['', Validators.required],
      status: [null, Validators.required]
    });
  }

  // =====================================================
  // INIT
  // =====================================================
  ngOnInit(): void {
    this.loadAssignMap();
    this.getCargo();
    this.getDrivers();
  }

  // =====================================================
  // LOCAL STORAGE (ASSIGNED DRIVER)
  // =====================================================
  private loadAssignMap(): void {
    try {
      const raw = localStorage.getItem(this.ASSIGN_MAP_KEY);
      this.assignedDriverMap = raw ? JSON.parse(raw) : {};
    } catch {
      this.assignedDriverMap = {};
    }
  }

  private saveAssignMap(): void {
    localStorage.setItem(this.ASSIGN_MAP_KEY, JSON.stringify(this.assignedDriverMap));
  }

  // =====================================================
  // FETCH CARGO
  // =====================================================
  getCargo(): void {
    this.showError = false;

    this.httpService.getCargo().subscribe({
      next: (data: any[]) => {
        this.cargList = data || [];
        this.cargoToShow = [...this.cargList];

        this.cargoToShow.forEach(cargo => {
          const cid = Number(cargo?.id);
          if (!cid) return;

          // restore assigned driver
          if (this.assignedDriverMap[cid]) {
            cargo.driver = cargo.driver || {};
            cargo.driver.id = this.assignedDriverMap[cid];

            if (cargo.status === 'Order Pending') {
              cargo.status = 'Order Assigned';
            }
          }

          // attach payment info
          this.httpService.getPaymentByCargoId(cid).subscribe({
            next: (payment: any) => cargo.payment = payment,
            error: () => cargo.payment = null
          });
        });
      },
      error: () => {
        this.showError = true;
        this.errorMessage = 'Cannot fetch cargo. Please try again later.';
      }
    });
  }

  // =====================================================
  // FETCH DRIVERS
  // =====================================================
  getDrivers(): void {
    this.httpService.getDrivers().subscribe({
      next: (data: any[]) => this.driverList = data || [],
      error: () => {
        this.showError = true;
        this.errorMessage = 'Cannot fetch drivers.';
      }
    });
  }

  // =====================================================
  // SEARCH
  // =====================================================
  search(): void {
    this.showError = false;

    const value = (this.cargoId || '').trim();

    if (!value) {
      this.cargoToShow = [...this.cargList];
      return;
    }

    if (/^\d{11}$/.test(value)) {
      this.httpService.getCargoByAwb(value).subscribe({
        next: (data: any) => this.cargoToShow = data ? [data] : [],
        error: () => {
          this.showError = true;
          this.errorMessage = 'No record found with entered A.W.B number';
        }
      });
      return;
    }

    this.httpService.getCargoById(value).subscribe({
      next: (data: any) => this.cargoToShow = data ? [data] : [],
      error: () => {
        this.showError = true;
        this.errorMessage = 'No record found with entered Cargo ID';
      }
    });
  }

  // =====================================================
  // ADD CARGO
  // =====================================================
  onSubmit(): void {
    this.addSuccess = false;
    this.addError = false;

    if (!this.selectedFiles.length) {
      this.addError = true;
      this.addErrorMessage = 'Please upload at least one document.';
      return;
    }

    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    const formData = new FormData();
    formData.append(
      'cargo',
      new Blob([JSON.stringify(this.itemForm.value)], { type: 'application/json' })
    );

    this.selectedFiles.forEach(file =>
      formData.append('documents', file, file.name)
    );

    this.httpService.addCargoWithDocuments(formData).subscribe({
      next: () => {
        this.addSuccess = true;
        this.addMessage = 'Cargo added successfully';
        this.itemForm.reset();
        this.selectedFiles = [];
        this.getCargo();
      },
      error: () => {
        this.addError = true;
        this.addErrorMessage = 'Failed to add cargo with documents';
      }
    });
  }

  // =====================================================
  // FILE UPLOAD
  // =====================================================
  onFileSelected(event: any): void {
    const files: File[] = Array.from(event.target.files || []);

    for (const file of files) {
      if (file.size > 10 * 1024 * 1024) {
        alert('File size must be less than 10 MB');
        this.selectedFiles = [];
        return;
      }
    }

    this.selectedFiles = files;
  }

  // =====================================================
  // ASSIGN DRIVER
  // =====================================================
  addDriver(cargo: any): void {
    this.driverId = null;
    cargo._selected = true;
    this.assignError = false;
  }

  assignDriver(): void {
    if (!this.driverId) {
      this.assignError = true;
      this.assignErrorMessage = 'Please select a driver before assigning.';
      return;
    }

    const cargo = this.cargoToShow.find(c => c._selected);
    if (!cargo) return;

    const cid = Number(cargo.id);

    this.httpService.assignDriver(this.driverId, cid).subscribe({
      next: (data: any) => {
        this.assignSuccess = true;
        this.assignMessage = data.message || 'Driver assigned successfully';

        this.assignedDriverMap[cid] = this.driverId!;
        this.saveAssignMap();
        this.getCargo();

        const modalEl = document.getElementById('driverModal');
        bootstrap.Modal.getInstance(modalEl)?.hide();
      },
      error: () => {
        this.assignError = true;
        this.assignErrorMessage = 'An error occurred while assigning driver.';
      }
    });
  }

  // =====================================================
  // PAYMENT
  // =====================================================
  payNow(cargo: any): void {
    if (!cargo?.driver?.id) {
      this.showError = true;
      this.errorMessage = 'Assign a driver before payment.';
      return;
    }

    this.router.navigate(['/payment'], {
      queryParams: {
        cargoId: cargo.id,
        driverId: cargo.driver.id
      }
    });
  }

  // =====================================================
  // LOGOUT
  // =====================================================
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}