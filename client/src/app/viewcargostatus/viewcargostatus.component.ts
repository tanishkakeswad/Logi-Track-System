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
  enteredAwb: string = '';
  showResult: boolean = false;

  showTracking: boolean = false;
  private statusKey: string = '';

  constructor(
    private fb: FormBuilder,
    private httpService: HttpService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
  this.cargoForm = this.fb.group({
    awb: [
      '',
      [
        Validators.required,
        Validators.pattern('^[0-9]{11}$') // ✅ exactly 11 digits
      ]
    ]
  });
}

  getStatus() {
  this.showResult = false;
  this.errorMessage = '';

  if (this.cargoForm.invalid) {
    this.errorMessage = 'Enter valid 11-digit A.W.B number';
    return;
  }

  this.enteredAwb = this.cargoForm.value.awb;

  this.httpService.getOrderStatusByAwb(this.enteredAwb).subscribe({
    next: (res) => {
      this.cargoData = res;
      this.statusKey = this.normalizeToStepKey(this.cargoData?.status);
      this.showResult = true;
    },
    error: () => {
      this.errorMessage = 'A.W.B number not found';
      this.showResult = false;
    }
  });
}

  openTracking() { this.showTracking = true; }
  closeTracking() { this.showTracking = false; }

  private normalizeToStepKey(rawStatus: any): string {
    if (!rawStatus) return 'CONFIRMED';

    const s = String(rawStatus).trim().toUpperCase().replace(/[-\s]+/g, '_');

    if (s === 'ORDER_PENDING' || s === 'PENDING') return 'CONFIRMED';
    if (s === 'ORDER_ASSIGNED' || s === 'ASSIGNED') return 'CONFIRMED';
    if (s === 'ORDER_IN_TRANSIT' || s === 'IN_TRANSIT') return 'IN_TRANSIT';
    if (s === 'OUT_FOR_DELIVERY') return 'OUT_FOR_DELIVERY';
    if (s === 'ORDER_DELIVERED' || s === 'DELIVERED') return 'ORDER_DELIVERED';

    return 'CONFIRMED';
  }

  isStepActive(step: string): boolean {
    const order = ['CONFIRMED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'ORDER_DELIVERED'];
    return order.indexOf(this.statusKey) >= order.indexOf(step);
  }

  isLineActive(step: string): boolean {
    return this.isStepActive(step);
  }

  getDisplayStatus(): string {
    if (this.cargoData?.status) return this.cargoData.status;
    return this.statusKey;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}