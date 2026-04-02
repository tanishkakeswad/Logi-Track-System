import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-viewcargostatus',
  templateUrl: './viewcargostatus.component.html',
  styleUrls: ['./viewcargostatus.component.scss']
})
export class ViewcargostatusComponent implements OnInit {

  cargoForm!: FormGroup;
  cargoData: any;
  errorMessage: string = '';
  showResult: boolean = false;

  showTracking: boolean = false;

  constructor(
    private fb: FormBuilder,
    private httpService: HttpService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargoForm = this.fb.group({
      cargoId: ['', Validators.required]
    });
  }

  getStatus() {
  if (this.cargoForm.invalid) {
    this.errorMessage = 'Please enter Cargo ID';
    return;
  }

  const cargoId = this.cargoForm.value.cargoId;

  this.httpService.getOrderStatus(cargoId).subscribe({
    next: (res) => {
      this.cargoData = res;

      // 🔥 normalize status
      if (this.cargoData?.status) {
        this.cargoData.status = this.cargoData.status
          .toUpperCase()
          .replace(/\s+/g, '_');
      }

      this.showResult = true;
      this.errorMessage = '';
    },
    error: () => {
      this.errorMessage = 'Cargo not found or error occurred';
      this.showResult = false;
    }
  });
}

  // 🔥 tracking modal
  openTracking() {
    this.showTracking = true;
  }

  closeTracking() {
    this.showTracking = false;
  }

  // 🔥 STEP LOGIC (CLEAN)
  isStepActive(step: string): boolean {
    if (!this.cargoData) return false;

    const order = ['CONFIRMED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'ORDER_DELIVERED'];
    return order.indexOf(this.cargoData.status) >= order.indexOf(step);
  }

  isLineActive(step: string): boolean {
    return this.isStepActive(step);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}