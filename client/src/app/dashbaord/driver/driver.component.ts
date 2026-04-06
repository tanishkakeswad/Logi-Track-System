import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-driver',
  templateUrl: './driver.component.html',
  styleUrls: ['./driver.component.scss']
})
export class DriverComponent implements OnInit, OnDestroy {

  watchId: any; // :fire: to stop tracking later

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    console.log("driver page load");
    if (!this.authService.getLoginStatus) {
      this.router.navigateByUrl('/login');
    }

    this.startTracking(); // :rocket: START GPS
  }

  ngOnDestroy(): void {
    // :fire: Stop GPS when leaving page
    if (this.watchId) {
      navigator.geolocation.clearWatch(this.watchId);
    }
  }

  startTracking() {

    if (!navigator.geolocation) {
      alert("Geolocation not supported");
      return;
    }

    this.watchId = navigator.geolocation.watchPosition(
      (position) => {

        const data = {
          driverId: 1, // TODO: make dynamic later
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };

        // :fire: DEBUG LOG (VERY IMPORTANT)
        console.log("Sending location:", data);

        fetch('/project/3567/proxy/3000/api/location/update', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(data)
        })
          .then(res => {
            if (!res.ok) {
              throw new Error("API failed");
            }
            console.log("Location sent successfully");
          })
          .catch(err => {
            console.error("Error sending location:", err);
          });

      },
      (error) => {
        console.error("GPS error:", error);
      },
      {
        enableHighAccuracy: true,
        maximumAge: 0,
        timeout: 5000
      }
    );
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}