import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';
import { Subject, takeUntil } from 'rxjs';

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

    // :fire: NEW
    attemptMessage: string = '';
    isBlocked: boolean = false;
    isLoading: boolean | undefined;

    constructor(
        private router: Router,
        private httpService: HttpService,
        private fb: FormBuilder
    ) {

        this.itemForm = this.fb.group({
            username: ['', [Validators.required, Validators.pattern("^[a-zA-Z][a-zA-Z0-9]{4,18}$")]],
            email: ['', [Validators.required, Validators.email]],
            otp: ['', Validators.required],
            password: ['', [Validators.required, Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@$!%*?&]).{8,}$")]],
            retypepassword: ['', Validators.required],
            role: [null, Validators.required]
        }, {
            validators: this.matchPassword
        });
    }

    // Password match validator
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
    // SEND OTP
    // =========================
    private destroy$ = new Subject<void>(); // Clean up on ngOnDestroy

    sendOtp() {
        const email = this.itemForm.get('email')?.value; // Added quotes

        if (!email) {
            this.responseMessage = 'Please enter email first';
            this.errorMessage = true;
            return;
        }

        this.isLoading = true; // Prevent spamming
        this.httpService.sendOtp(email)
            .pipe(takeUntil(this.destroy$)) // Prevent memory leaks
            .subscribe({
                next: () => {
                    this.isLoading = false;
                    this.showMessage = true;
                    this.errorMessage = false;
                    this.responseMessage = 'OTP sent to your email';
                    this.isBlocked = false;
                },
                error: (err) => {
                    this.isLoading = false;
                    this.errorMessage = true;
                    this.showMessage = false;
                    this.responseMessage = 'Failed to send OTP';
                }
            });
    }

    // =========================
    // REGISTER
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
            otp: this.itemForm.value.otp
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

                const msg = err.error;

                // :fire: SHOW ATTEMPTS MESSAGE
                this.attemptMessage = msg;

                // :fire: BLOCK after max attempts
                if (msg && msg.includes('Maximum attempts')) {
                    this.isBlocked = true;
                }

                // Keep other errors
                if (err.status === 409) {
                    this.responseMessage = 'Username already exists';
                } else if (!msg) {
                    this.responseMessage = 'Something went wrong. Try again';
                }
            }
        });
    }
}