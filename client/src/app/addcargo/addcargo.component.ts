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

  // ✅ Keep existing ones (used elsewhere)
  showError: boolean = false;
  errorMessage: any;

  cargList: any = [];
  assignModel: any = {};
  driverList: any = [];
  driverId: any;
  cargoId: any;
  cargoToShow: any[] = [];
  selectedFiles: File[] = [];

  // ✅ NEW: separate messages
  addSuccess: boolean = false;
  addMessage: string = '';
  addError: boolean = false;
  addErrorMessage: string = '';

  assignSuccess: boolean = false;
  assignMessage: string = '';
  assignError: boolean = false;
  assignErrorMessage: string = '';

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
    this.getCargo();
    this.getDrivers();
    this.driverId = null;
  }

  getCargo() {
    this.cargList = [];
    this.httpService.getCargo().subscribe(
      (data: any) => {
        this.cargList = data;
        this.cargoToShow = this.cargList;
      },
      error => {
        this.showError = true;
        this.errorMessage = "Cannot fetch cargo. Please try again later.";
        console.error('Error:', error);
      }
    );
  }

  getDrivers() {
    this.driverList = [];
    this.httpService.getDrivers().subscribe(
      (data: any) => {
        this.driverList = data;
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
          this.cargoToShow = [data];
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
    // reset assign alerts (optional)
    this.assignSuccess = false;
    this.assignError = false;
    this.assignMessage = '';
    this.assignErrorMessage = '';

    // reset add alerts
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

    // reset assign messages when opening modal
    this.assignSuccess = false;
    this.assignError = false;
    this.assignMessage = '';
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

    // reset assign alerts
    this.assignSuccess = false;
    this.assignError = false;
    this.assignMessage = '';
    this.assignErrorMessage = '';

    if (this.assignModel.driverId != null) {
      this.httpService.assignDriver(this.assignModel.driverId, this.assignModel.cargoId).subscribe(
        (data: any) => {
          this.assignSuccess = true;
          this.assignMessage = data.message || 'Cargo assigned successfully';

          // refresh list without reloading
          this.getCargo();

          // close modal
          const modalEl = document.getElementById('driverModal');
          if (modalEl) {
            const modalInstance = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
            modalInstance.hide();
          }

          this.driverId = null;

          // auto-hide success after 3 seconds (optional)
          setTimeout(() => {
            this.assignSuccess = false;
            this.assignMessage = '';
          }, 3000);
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
