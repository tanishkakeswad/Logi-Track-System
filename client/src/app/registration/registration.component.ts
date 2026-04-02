import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent {

  itemForm!: FormGroup;
  showMessage = false;
  errorMessage = false;
  responseMessage = '';
  passwordFieldType: string = 'password';

  constructor(
    private router: Router,
    private httpService: HttpService,
    private fb: FormBuilder
  ) {

    this.itemForm = this.fb.group({
      username: ['', [Validators.required, Validators.pattern("^[a-zA-Z][a-zA-Z0-9]{4,18}$")]],
      email: ['', [Validators.required, Validators.email]],
      otp: ['', Validators.required], // :white_check_mark: NEW FIELD
      password: ['', [Validators.required, Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@$!%*?&]).{8,}$")]],
      retypepassword: ['', Validators.required],
      role: [null, Validators.required]
    }, {
      validators: this.matchPassword
    });
  }

  // :white_check_mark: Password match validator
  matchPassword(control: AbstractControl): ValidationErrors | null {
    const pass = control.get('password')?.value;
    const confirm = control.get('retypepassword')?.value;

    if (pass === confirm) {
      return null;
    }
    return { notMatch: true };
  }

  togglePasswordVisibility() {
    this.passwordFieldType =
      this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  // =========================
  // :white_check_mark: SEND OTP
  // =========================
  sendOtp() {
    const email = this.itemForm.get('email')?.value;

    if (!email) {
      this.responseMessage = "Please enter email first";
      this.errorMessage = true;
      return;
    }

    this.httpService.sendOtp(email).subscribe({
      next: () => {
        this.showMessage = true;
        this.errorMessage = false;
        this.responseMessage = "OTP sent to your email";
      },
      error: () => {
        this.errorMessage = true;
        this.showMessage = false;
        this.responseMessage = "Failed to send OTP";
      }
    });
  }

  // =========================
  // :white_check_mark: REGISTER
  // =========================
  onRegister() {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    this.showMessage = false;
    this.errorMessage = false;

    const formData = {
      username: this.itemForm.value.username,
      email: this.itemForm.value.email,
      password: this.itemForm.value.password,
      role: this.itemForm.value.role,
      otp: this.itemForm.value.otp // :white_check_mark: IMPORTANT
    };

    this.httpService.registerUser(formData).subscribe({
      next: (data: any) => {
        this.showMessage = true;
        this.responseMessage = `Welcome ${data.name}, you are successfully registered`;

        this.itemForm.reset();

        setTimeout(() => {
          this.router.navigateByUrl('/login');
        }, 2000);
      },
      error: (err) => {
        console.log("Backend Error:", err);
        this.errorMessage = true;

        if (err.status === 400) {
          this.responseMessage = 'Invalid or expired OTP';
        } else if (err.status === 409) {
          this.responseMessage = 'Username already exists';
        } else {
          this.responseMessage = 'Something went wrong. Try again';
        }
      }
    });
  }
}