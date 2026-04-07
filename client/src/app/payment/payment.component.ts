import { Component,OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpService } from '../../services/http.service'
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { NgZone } from '@angular/core';

declare var Razorpay: any;

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.scss']
})
export class PaymentComponent implements OnInit {

  supportedCities = [
    'New Delhi', 'Mumbai', 'Kolkata', 'Chennai', 'Bengaluru', 'Hyderabad', 'Pune'
  ];

  loading = false;
  message = '';
  previewAmountInPaise: number | null = null;

  // ✅ Keep control name "size" but label it as "Weight (KG)"
  paymentForm = this.fb.group({
  sourceCity: this.fb.control<string | null>('', Validators.required),
  destinationCity: this.fb.control<string | null>('', Validators.required),

  size: this.fb.control<number | null>(null, [
    Validators.required,
    Validators.min(1),
    Validators.max(10000)
  ]),

  driverId: this.fb.control<number | null>(null, Validators.required),
  cargoId: this.fb.control<number | null>(null, Validators.required)
});

  constructor(private fb: FormBuilder, private httpService: HttpService, private route: ActivatedRoute,private router:Router,private ngZone: NgZone) {}

  pay() {
    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.message = '';

    const raw = this.paymentForm.getRawValue();

const payload = {
  sourceCity: raw.sourceCity!,
  destinationCity: raw.destinationCity!,
  size: raw.size!,          // ✅ weight (KG)
  driverId: raw.driverId!,  // ✅ auto-filled
  cargoId: raw.cargoId!     // ✅ auto-filled
};


    this.httpService.createOrder(payload).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.previewAmountInPaise = res.amount;

        const options = {
          key: res.razorpayKey,
          amount: res.amount,
          currency: res.currency,
          name: 'CargoWala',
          description: 'Cargo Delivery Payment',
          order_id: res.orderId,
          handler: (response: any) => {
            this.verify(res.paymentRecordId, response);
          }
        };

        const rzp = new Razorpay(options);
        rzp.open();
      },
      error: (err) => {
        this.loading = false;
        this.message = err?.error?.message || 'Failed to create Razorpay order';
      }
    });
  }

  private verify(paymentRecordId: number, response: any) {

  const payload = {
    paymentRecordId,
    razorpayOrderId: response.razorpay_order_id,
    razorpayPaymentId: response.razorpay_payment_id,
    razorpaySignature: response.razorpay_signature
  };

  this.httpService.verifyPayment(payload).subscribe({
    next: () => {
      this.message = `✅ Payment successful!
Reference: ${response.razorpay_payment_id}`;

      // ✅ FIX: force Angular zone
      setTimeout(() => {
        this.ngZone.run(() => {
          this.router.navigate(['/addcargo']);
        });
      }, 1500);
    },
    error: (err) => {
      this.message = err?.error?.message || '❌ Payment verification failed';
    }
  });
}


  ngOnInit(): void {
  this.route.queryParams.subscribe(params => {
    const cargoId = params['cargoId'];
    const driverId = params['driverId'];

    if (cargoId) {
      this.paymentForm.patchValue({ cargoId: Number(cargoId) });
      this.paymentForm.get('cargoId')?.disable(); // prevent edit
    }

    if (driverId) {
      this.paymentForm.patchValue({ driverId: Number(driverId) });
      this.paymentForm.get('driverId')?.disable(); // prevent edit
    }
  });
}

}