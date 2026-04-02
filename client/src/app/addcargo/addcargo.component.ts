import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';
import { AuthService } from '../../services/auth.service';



@Component({
  selector: 'app-addcargo',
  templateUrl: './addcargo.component.html',
  styleUrls: ['./addcargo.component.scss']
})
export class AddcargoComponent {
  itemForm: FormGroup;
  itemForm1: FormGroup;
  formModel: any = { status: null };
  showError: boolean = false;
  errorMessage: any;
  cargList: any = [];
  assignModel: any = {};
  driverList: any = []
  showMessage: any;
  responseMessage: any;
  driverId: any;
  cargoId: any
  cargoToShow: any[] = [];
  selectedFiles: File[] = [];
  constructor(public router: Router, public httpService: HttpService, private formBuilder: FormBuilder, private authService: AuthService) {
    this.itemForm = this.formBuilder.group({
      content: [this.formModel.content, [Validators.required]],
      size: [this.formModel.size, [Validators.required]],
      status: [this.formModel.status, [Validators.required]]
    });
    this.itemForm1 = this.formBuilder.group({
      driver: [this.formModel.driver]
    })
  }
  ngOnInit(): void {
    this.getCargo();
    this.getDrivers();
    this.driverId = null;
  }
  getCargo() {
    this.cargList = [];
    this.httpService.getCargo().subscribe((data: any) => {
      this.cargList = data;
      this.cargoToShow = this.cargList;
    }, error => {
      // Handle error
      this.showError = true;
      this.errorMessage = "Cannot fetch cargo. Please try again later.";
      console.error('Error:', error);
    });;
  }

  getDrivers() {
    this.driverList = [];
    this.httpService.getDrivers().subscribe((data: any) => {
      this.driverList = data;
      console.log(this.driverList);
    }, error => {
      // Handle error
      this.showError = true;
      this.errorMessage = "Cannot get Drivers. Please try again later.";
      console.error('Error:', error);
    });;

  }
  search() {
    this.showError = false;
    if (this.cargoId) {
      this.httpService.getCargoById(this.cargoId).subscribe((data: any) => {
        this.cargoToShow = [data];
      }, error => {
        // Handle error
        this.showError = true;
        this.errorMessage = "No Record found with entered search ID";
        console.error('Login error:', error);
      });;
    }
    else {
      this.cargoToShow = this.cargList;
    }
  }

onSubmit() {
  if (this.itemForm.valid) {
    this.showError = false;

    const formData = new FormData();

    // Append cargo JSON
    formData.append(
      'cargo',
      new Blob([JSON.stringify(this.itemForm.value)], {
        type: 'application/json'
      })
    );

    // Append documents
    this.selectedFiles.forEach(file => {
      formData.append('documents', file, file.name);
    });

    this.httpService.addCargoWithDocuments(formData).subscribe(
      (data: any) => {
        this.itemForm.reset();
        this.selectedFiles = [];
        this.getCargo();
      },
      error => {
        this.showError = true;
        this.errorMessage = 'Failed to add cargo with documents';
        console.error(error);
      }
    );
  } else {
    this.itemForm.markAllAsTouched();
  }
}
  addDriver(value: any) {
    this.assignModel.cargoId = value.id
  }

onFileSelected(event: any) {
  if (event.target.files && event.target.files.length > 0) {
    this.selectedFiles = Array.from(event.target.files);
  }
}

  assignDriver() {
    console.log("assigning")
    this.assignModel.driverId = this.driverId;
    console.log(this.assignModel.driverId)
    if (this.assignModel.driverId != null) {
      this.showMessage = false;
      this.httpService.assignDriver(this.assignModel.driverId, this.assignModel.cargoId).subscribe((data: any) => {
        this.showMessage = true;
        this.responseMessage = data.message;
        window.location.reload();
      }, error => {
        // Handle error
        this.showError = true;
        this.errorMessage = "An error occurred while assigning driver. Please try again later.";
        console.error('Error:', error);
      });;
    }
  }
}


//todo: Complete missing code.. 
