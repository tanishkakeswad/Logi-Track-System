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
  private statusKey: string = '';

  constructor(
    private fb: FormBuilder,
    private httpService: HttpService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargoForm = this.fb.group({
      // accept digits only (awb or id)
      cargoId: ['', [Validators.required, Validators.pattern('^[0-9]{1,18}$')]]
    });
  }

  getStatus() {
    if (this.cargoForm.invalid) {
      this.errorMessage = 'Please enter A.W.B / Cargo ID';
      this.showResult = false;
      return;
    }

    const raw = String(this.cargoForm.value.cargoId).trim();
    const isAwb = /^[0-9]{11}$/.test(raw);

    const request$ = isAwb
      ? this.httpService.getOrderStatusByAwb(raw)
      : this.httpService.getOrderStatus(raw);

    request$.subscribe({
      next: (res) => {
        this.cargoData = res;
        this.statusKey = this.normalizeToStepKey(this.cargoData?.status);
        this.showResult = true;
        this.errorMessage = '';
      },
      error: () => {
        this.errorMessage = 'Cargo not found or error occurred';
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