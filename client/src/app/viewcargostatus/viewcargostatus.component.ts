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

  // ✅ Fetch Cargo Status
  getStatus() {
    if (this.cargoForm.invalid) {
      this.errorMessage = 'Please enter Cargo ID';
      return;
    }

    const cargoId = this.cargoForm.value.cargoId;

    this.httpService.getOrderStatus(cargoId).subscribe({
      next: (res) => {
        this.cargoData = res;
        this.showResult = true;
        this.errorMessage = '';
      },
      error: (err) => {
        this.errorMessage = 'Cargo not found or error occurred';
        this.showResult = false;
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}