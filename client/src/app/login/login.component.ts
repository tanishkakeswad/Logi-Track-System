import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, AfterViewInit {
  @ViewChild('captchaCanvas') captchaCanvas!: ElementRef<HTMLCanvasElement>;

  itemForm: FormGroup;
  formModel: any = {};
  showError: boolean = false;
  errorMessage: any;

  // CAPTCHA
  captchaText: string = '';
  captchaInput: string = '';
  captchaValid: boolean = false;
  captchaError: boolean = false;

  constructor(
    public router: Router,
    public httpService: HttpService,
    private formBuilder: FormBuilder,
    private authService: AuthService
  ) {
    this.itemForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.captchaText = this.generateRandomText();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.drawCaptcha(), 100);
  }

  generateRandomText(): string {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789';
    let result = '';
    for (let i = 0; i < 6; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }

  drawCaptcha(): void {
    const canvas = this.captchaCanvas?.nativeElement;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const W = canvas.width;
    const H = canvas.height;

    // Background — dark navy matching .custom-cargo-color (#003049)
    const bgGrad = ctx.createLinearGradient(0, 0, W, H);
    bgGrad.addColorStop(0, '#003049');
    bgGrad.addColorStop(1, '#00486e');
    ctx.fillStyle = bgGrad;
    ctx.fillRect(0, 0, W, H);

    // Noise dots
    for (let i = 0; i < 90; i++) {
      ctx.beginPath();
      ctx.arc(Math.random() * W, Math.random() * H, Math.random() * 1.5, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255,255,255,${Math.random() * 0.3})`;
      ctx.fill();
    }

    // Interference bezier lines — orange brand color (#f77f00)
    for (let i = 0; i < 5; i++) {
      ctx.beginPath();
      ctx.moveTo(Math.random() * W, Math.random() * H);
      ctx.bezierCurveTo(
        Math.random() * W, Math.random() * H,
        Math.random() * W, Math.random() * H,
        Math.random() * W, Math.random() * H
      );
      ctx.strokeStyle = `rgba(247, 127, 0, ${Math.random() * 0.45 + 0.2})`;
      ctx.lineWidth = Math.random() * 1.5 + 0.5;
      ctx.stroke();
    }

    // Characters — orange/white/gold using brand palette
    const charColors = ['#f77f00', '#ffffff', '#ffd166', '#ff9f1c', '#f77f00', '#ffffff'];
    const fonts = [
      'bold 24px Georgia',
      'bold 22px "Courier New"',
      'italic bold 25px serif',
      'bold 23px "Times New Roman"'
    ];
    const startX = 18;
    const charSpacing = (W - 36) / this.captchaText.length;

    for (let i = 0; i < this.captchaText.length; i++) {
      const x = startX + i * charSpacing + charSpacing / 2;
      const y = H / 2 + (Math.random() * 10 - 5);
      const angle = (Math.random() - 0.5) * 0.5;

      ctx.save();
      ctx.translate(x, y);
      ctx.rotate(angle);
      ctx.font = fonts[Math.floor(Math.random() * fonts.length)];
      ctx.fillStyle = charColors[i % charColors.length];
      ctx.shadowColor = 'rgba(0,0,0,0.7)';
      ctx.shadowBlur = 4;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(this.captchaText[i], 0, 0);
      ctx.restore();
    }

    // Border — brand orange
    ctx.strokeStyle = 'rgba(247, 127, 0, 0.7)';
    ctx.lineWidth = 1.5;
    ctx.strokeRect(0, 0, W, H);
  }

  refreshCaptcha(): void {
    this.captchaText = this.generateRandomText();
    this.captchaInput = '';
    this.captchaValid = false;
    this.captchaError = false;
    setTimeout(() => this.drawCaptcha(), 50);
  }

  verifyCaptcha(): void {
    if (!this.captchaInput.trim()) {
      this.captchaValid = false;
      this.captchaError = false;
      return;
    }
    if (this.captchaInput.trim() === this.captchaText) {
      this.captchaValid = true;
      this.captchaError = false;
    } else {
      this.captchaValid = false;
      this.captchaError = true;
    }
  }

  get isLoginDisabled(): boolean {
    return this.itemForm.invalid || !this.captchaValid;
  }

  onLogin() {
    if (this.itemForm.valid && this.captchaValid) {
      this.showError = false;
      const loginDetails = this.itemForm.value;
      this.httpService.Login(loginDetails).subscribe({
        next: (response: any) => {
          const role = response.role?.trim().toUpperCase();
          this.authService.saveToken(response.token);
          this.authService.setRole(role);
          this.authService.setId(response.id);
          if (role === 'BUSINESS') {
            this.router.navigateByUrl('/dashboard/business');
          } else if (role === 'DRIVER') {
            this.router.navigateByUrl('/dashboard/driver');
          } else if (role === 'CUSTOMER') {
            this.router.navigateByUrl('/dashboard/customer');
          } else {
            this.router.navigateByUrl('/');
          }
        },
        error: () => {
          this.showError = true;
          this.errorMessage = 'Invalid username or password';
          this.refreshCaptcha();
        }
      });
    } else {
      this.showError = true;
      this.errorMessage = !this.captchaValid
        ? 'Please verify the captcha correctly'
        : 'Please fill in all required fields';
      this.itemForm.markAllAsTouched();
    }
  }

  registration() {
    this.router.navigateByUrl('registration');
  }
}