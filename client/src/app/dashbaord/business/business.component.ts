import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

declare let L: any;

@Component({
  selector: 'app-business',
  templateUrl: './business.component.html',
  styleUrls: ['./business.component.scss']
})
export class BusinessComponent implements OnInit, OnDestroy {

  map: any;
  markers: any[] = [];
  selectedDriverId: number = 0;
  intervalId: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (!this.authService.getLoginStatus) {
      this.router.navigateByUrl('/login');
    }
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  initMap() {
    if (!this.map) {
      this.map = L.map('businessMap').setView([19.0760, 72.8777], 11);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png')
        .addTo(this.map);

      // :fire: IMPORTANT FIX
      setTimeout(() => {
        this.map.invalidateSize();
      }, 300);
    }
  }

  clearMarkers() {
    this.markers.forEach(m => this.map.removeLayer(m));
    this.markers = [];
  }

  stopPreviousTracking() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  showAllDrivers() {
    this.initMap();
    this.stopPreviousTracking();

    this.intervalId = setInterval(() => {

      fetch('/project/3567/proxy/3000/api/location/all')
        .then(res => res.json())
        .then((data: any[]) => {

          console.log("ALL DRIVERS:", data);

          if (!data || data.length === 0) return;

          this.clearMarkers();

          data.forEach(d => {
            if (!d.lat || !d.lng) return;

            let marker = L.marker([d.lat, d.lng]).addTo(this.map);
            this.markers.push(marker);
          });

        });

    }, 3000);
  }

  trackOneDriver() {
    if (!this.selectedDriverId) {
      alert("Enter Driver ID");
      return;
    }

    this.initMap();
    this.stopPreviousTracking();

    this.intervalId = setInterval(() => {

      fetch(`/project/3567/proxy/3000/api/location/${this.selectedDriverId}`)
        .then(res => res.json())
        .then(data => {

          console.log("ONE DRIVER:", data);

          if (!data) return;

          this.clearMarkers();

          let marker = L.marker([data.lat, data.lng]).addTo(this.map);
          this.markers.push(marker);

          this.map.setView([data.lat, data.lng], 13);

        });

    }, 3000);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}