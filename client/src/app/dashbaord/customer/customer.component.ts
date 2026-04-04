
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { Component, OnInit } from '@angular/core';

declare let L: any;

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {

  driverId: number = 1;
  map: any;
  marker: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (!this.authService.getLoginStatus) {
      this.router.navigateByUrl('/login');
    }
  }

  startTracking() {

    if (!this.driverId) {
      alert("Enter Driver ID");
      return;
    }

    if (!this.map) {
      this.map = L.map('customerMap').setView([19.0760, 72.8777], 12);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png')
        .addTo(this.map);
    }

    setInterval(() => {

      fetch(`/project/3567/proxy/3000/api/location/${this.driverId}`)
        .then(res => res.json())
        .then(data => {

          if (!data) return;

          if (!this.marker) {
            this.marker = L.marker([data.lat, data.lng]).addTo(this.map);
          } else {
            this.marker.setLatLng([data.lat, data.lng]);
          }

          this.map.setView([data.lat, data.lng]);

        });

    }, 3000);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}