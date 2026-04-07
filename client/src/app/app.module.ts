import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';

import { HttpService } from '../services/http.service';

import { AddcargoComponent } from './addcargo/addcargo.component';
import { AssginCargoComponent } from './assgin-cargo/assgin-cargo.component';
import { ViewcargostatusComponent } from './viewcargostatus/viewcargostatus.component';

import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { LandingComponent } from './landing/landing.component';
import { AboutComponent } from './about/about.component';
import { ContactComponent } from './contact/contact.component';

import { DriverComponent } from './dashbaord/driver/driver.component';
import { BusinessComponent } from './dashbaord/business/business.component';
import { CustomerComponent } from './dashbaord/customer/customer.component';

import { ChatbotComponent } from './chatbot/chatbot.component';

import { PaymentComponent } from './payment/payment.component';
// If you need it later, uncomment these two lines
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
    ContactComponent,
    ChatbotComponent,

    // DriverTrackingComponent

    PaymentComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    HttpService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }