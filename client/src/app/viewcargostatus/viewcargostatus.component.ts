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

  // ✅ internal normalized key used by tracking UI
  private statusKey: string = '';

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
      this.showResult = false;
      return;
    }

    const cargoId = this.cargoForm.value.cargoId;

    this.httpService.getOrderStatus(cargoId).subscribe({
      next: (res) => {
        this.cargoData = res;

        // ✅ Normalize + map backend status to tracking step keys
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

  // 🔥 tracking modal
  openTracking() {
    this.showTracking = true;
  }

  closeTracking() {
    this.showTracking = false;
  }

  /**
   * ✅ Map ANY backend status into our stepper-friendly keys.
   * Works with:
   * - "Order Pending" / "ORDER_PENDING"
   * - "Order Assigned" / "ORDER_ASSIGNED"
   * - "Order In-transit" / "ORDER_IN_TRANSIT"
   * - "Order Delivered" / "ORDER_DELIVERED"
   * Also supports:
   * - "CONFIRMED", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"
   */
  private normalizeToStepKey(rawStatus: any): string {
    if (!rawStatus) return 'CONFIRMED';

    const s = String(rawStatus)
      .trim()
      .toUpperCase()
      .replace(/[-\s]+/g, '_'); // spaces/hyphens → underscore

    // ✅ Map common backend strings
    if (s === 'ORDER_PENDING' || s === 'PENDING') return 'CONFIRMED';
    if (s === 'ORDER_ASSIGNED' || s === 'ASSIGNED') return 'CONFIRMED';

    if (s === 'ORDER_IN_TRANSIT' || s === 'IN_TRANSIT' || s === 'ORDER_IN_TRANSIT') return 'IN_TRANSIT';
    if (s === 'ORDER_IN_TRANSIT' || s === 'ORDER_IN_TRANSIT') return 'IN_TRANSIT';

    // Handle your older label: ORDER_IN-TRANSIT becomes ORDER_IN_TRANSIT after replace
    if (s === 'ORDER_IN_TRANSIT') return 'IN_TRANSIT';

    if (s === 'OUT_FOR_DELIVERY') return 'OUT_FOR_DELIVERY';

    if (s === 'ORDER_DELIVERED' || s === 'DELIVERED') return 'ORDER_DELIVERED';

    // fallback: if unknown, show as confirmed so UI still works
    return 'CONFIRMED';
  }

  // ✅ STEP LOGIC (progressive)
  isStepActive(step: string): boolean {
    const order = ['CONFIRMED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'ORDER_DELIVERED'];
    return order.indexOf(this.statusKey) >= order.indexOf(step);
  }

  isLineActive(step: string): boolean {
    return this.isStepActive(step);
  }

  // ✅ Show a friendly status label on the result card
  getDisplayStatus(): string {
    // prefer backend original if present
    if (this.cargoData?.status) return this.cargoData.status;
    return this.statusKey;
  }

  // ✅ Logout (keep as-is; you can remove button from HTML if navbar already has it)
  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}