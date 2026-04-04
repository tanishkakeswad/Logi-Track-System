import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { RegistrationComponent } from './registration/registration.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpService } from '../services/http.service';
import { AddcargoComponent } from './addcargo/addcargo.component';
import { AssginCargoComponent } from './assgin-cargo/assgin-cargo.component';
import { ViewcargostatusComponent } from './viewcargostatus/viewcargostatus.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { LandingComponent } from './landing/landing.component';
import { AboutComponent } from './about/about.component';
import { DriverComponent } from './dashbaord/driver/driver.component';
import { BusinessComponent } from './dashbaord/business/business.component';
import { CustomerComponent } from './dashbaord/customer/customer.component';
// import { DriverTrackingComponent } from './driver-tracking/driver-tracking.component';


@NgModule({
  declarations: [
    AppComponent,
    LandingComponent,
    NavbarComponent,
    FooterComponent,
    LoginComponent,
      RegistrationComponent,
      DriverComponent,
      BusinessComponent,
      CustomerComponent,
      AddcargoComponent,
      AssginCargoComponent,
      ViewcargostatusComponent,
      AboutComponent,
      // DriverTrackingComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule 
  ],
  providers: [HttpService,HttpClientModule ],
  bootstrap: [AppComponent]
})
export class AppModule { }
