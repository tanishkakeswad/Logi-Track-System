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
  itemForm1: FormGroup;
  formModel: any = { status: null };

  showError: boolean = false;
  errorMessage: any;

  cargList: any = [];
  cargoToShow: any[] = [];

  assignModel: any = {};
  driverList: any = [];
  driverId: any;

  cargoId: any;
  selectedFiles: File[] = [];

  addSuccess: boolean = false;
  addMessage: string = '';
  addError: boolean = false;
  addErrorMessage: string = '';

  assignSuccess: boolean = false;
  assignMessage: string = '';
  assignError: boolean = false;
  assignErrorMessage: string = '';

  // ✅ Remember assigned driver per cargo even if backend doesn't return cargo.driver
  private assignedDriverMap: Record<number, number> = {};

  // ✅ Persist map so it survives navigation (pay later)
  private readonly ASSIGN_MAP_KEY = 'assignedDriverMap';

  constructor(
    public router: Router,
    public httpService: HttpService,
    private formBuilder: FormBuilder,
    private authService: AuthService
  ) {
    this.itemForm = this.formBuilder.group({
      content: [this.formModel.content, [Validators.required]],
      size: [this.formModel.size, [Validators.required]],
      status: [this.formModel.status, [Validators.required]]
    });

    this.itemForm1 = this.formBuilder.group({
      driver: [this.formModel.driver]
    });
  }

  ngOnInit(): void {
    // ✅ Load persisted assigned-driver map
    this.loadAssignMap();

    this.getCargo();
    this.getDrivers();
    this.driverId = null;
  }

  // ✅ Load map from localStorage
  private loadAssignMap(): void {
    try {
      const raw = localStorage.getItem(this.ASSIGN_MAP_KEY);
      this.assignedDriverMap = raw ? JSON.parse(raw) : {};
    } catch {
      this.assignedDriverMap = {};
    }
  }

  // ✅ Save map to localStorage
  private saveAssignMap(): void {
    localStorage.setItem(this.ASSIGN_MAP_KEY, JSON.stringify(this.assignedDriverMap));
  }

  getCargo() {
    this.cargList = [];
    this.showError = false;

    this.httpService.getCargo().subscribe({
      next: (data: any) => {
        this.cargList = data || [];
        this.cargoToShow = this.cargList;

        // ✅ Re-attach driver info from local map so Pay stays enabled
        this.cargoToShow.forEach(cargo => {
          const cid = Number(cargo?.id);
          if (!cid) return;

          if (this.assignedDriverMap[cid]) {
            cargo.driver = cargo.driver || {};
            cargo.driver.id = this.assignedDriverMap[cid];

            // Optional: bump status visually if backend still says Pending
            if (cargo.status === 'Order Pending') {
              cargo.status = 'Order Assigned';
            }
          }

          // ✅ Attach payment info
          // @ts-ignore
          this.httpService.getPaymentByCargoId(cid).subscribe({
            next: (payment: any) => {
              cargo.payment = payment;
            },
            error: () => {
              cargo.payment = null;
            }
          });
        });
      },
      error: (error) => {
        this.showError = true;
        this.errorMessage = "Cannot fetch cargo. Please try again later.";
        console.error('Error:', error);
      }
    });
  }

  getDrivers() {
    this.driverList = [];
    this.httpService.getDrivers().subscribe(
      (data: any) => {
        this.driverList = data || [];
      },
      error => {
        this.showError = true;
        this.errorMessage = "Cannot get Drivers. Please try again later.";
        console.error('Error:', error);
      }
    );
  }

  search() {
    this.showError = false;

    if (this.cargoId) {
      this.httpService.getCargoById(this.cargoId).subscribe(
        (data: any) => {
          this.cargoToShow = data ? [data] : [];

          // ✅ If searched cargo was assigned earlier, re-attach driver
          const cargo = this.cargoToShow[0];
          const cid = Number(cargo?.id);

          if (cid && this.assignedDriverMap[cid]) {
            cargo.driver = cargo.driver || {};
            cargo.driver.id = this.assignedDriverMap[cid];

            if (cargo.status === 'Order Pending') {
              cargo.status = 'Order Assigned';
            }
          }

          // ✅ Attach payment for searched cargo
          if (cid) {
            // @ts-ignore
            this.httpService.getPaymentByCargoId(cid).subscribe({
              next: (payment: any) => cargo.payment = payment,
              error: () => cargo.payment = null
            });
          }
        },
        error => {
          this.showError = true;
          this.errorMessage = "No Record found with entered search ID";
          console.error('Search error:', error);
        }
      );
    } else {
      this.cargoToShow = this.cargList;
    }
  }

  onSubmit() {
    this.addSuccess = false;
    this.addError = false;
    this.addMessage = '';
    this.addErrorMessage = '';

    if (this.itemForm.valid) {
      const formData = new FormData();

      formData.append(
        'cargo',
        new Blob([JSON.stringify(this.itemForm.value)], { type: 'application/json' })
      );

      this.selectedFiles.forEach(file => {
        formData.append('documents', file, file.name);
      });

      this.httpService.addCargoWithDocuments(formData).subscribe(
        () => {
          this.addSuccess = true;
          this.addMessage = 'Cargo added successfully';

          this.itemForm.reset();
          this.selectedFiles = [];
          this.getCargo();
        },
        error => {
          this.addError = true;
          this.addErrorMessage = 'Failed to add cargo with documents';
          console.error(error);
        }
      );
    } else {
      this.itemForm.markAllAsTouched();
    }
  }

  addDriver(value: any) {
    this.assignModel.cargoId = value.id;
    this.assignError = false;
    this.assignErrorMessage = '';
    this.driverId = null;
  }

  onFileSelected(event: any) {
    const files: File[] = Array.from(event.target.files);

    for (const file of files) {
      if (file.size > 10 * 1024 * 1024) {
        alert('File size must be less than 10 MB');
        event.target.value = '';
        this.selectedFiles = [];
        return;
      }
    }

    this.selectedFiles = files;
  }

  assignDriver() {
    this.assignModel.driverId = this.driverId;

    this.assignSuccess = false;
    this.assignError = false;
    this.assignMessage = '';
    this.assignErrorMessage = '';

    if (this.assignModel.driverId != null) {
      this.httpService.assignDriver(this.assignModel.driverId, this.assignModel.cargoId).subscribe(
        (data: any) => {
          this.assignSuccess = true;
          this.assignMessage = data.message || 'Driver assigned successfully';

          // ✅ bring alert into view
          window.scrollTo({ top: 0, behavior: 'smooth' });

          // ✅ store assignment so Pay stays enabled even after navigation
          const cid = Number(this.assignModel.cargoId);
          const did = Number(this.assignModel.driverId);
          if (cid && did) {
            this.assignedDriverMap[cid] = did;
            this.saveAssignMap(); // ✅ persist
          }

          // ✅ update current row immediately (instant UI)
          const updated = this.cargoToShow.find(c => Number(c.id) === cid);
          if (updated) {
            updated.driver = updated.driver || {};
            updated.driver.id = did;

            if (updated.status === 'Order Pending') {
              updated.status = 'Order Assigned';
            }
          }

          // ✅ refresh list (safe: driver is reattached from map)
          this.getCargo();

          // close modal
          const modalEl = document.getElementById('driverModal');
          if (modalEl) {
            const modalInstance = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
            modalInstance.hide();
          }

          this.driverId = null;

          setTimeout(() => {
            this.assignSuccess = false;
            this.assignMessage = '';
          }, 5000);
        },
        error => {
          this.assignError = true;
          this.assignErrorMessage = "An error occurred while assigning driver. Please try again later.";
          console.error('Error:', error);
        }
      );
    } else {
      this.assignError = true;
      this.assignErrorMessage = "Please select a driver before assigning.";
    }
  }

  payNow(cargo: any) {
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}