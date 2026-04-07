import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { AddcargoComponent } from './addcargo/addcargo.component';
// import { DashbaordComponent } from './dashbaord/dashbaord.component';
import { AssginCargoComponent } from './assgin-cargo/assgin-cargo.component';
import { ViewcargostatusComponent } from './viewcargostatus/viewcargostatus.component';
import { LandingComponent } from './landing/landing.component';
import { AboutComponent } from './about/about.component';
import { BusinessComponent } from './dashbaord/business/business.component';
import { DriverComponent } from './dashbaord/driver/driver.component';
import { CustomerComponent } from './dashbaord/customer/customer.component';
import { AuthGuard } from './guards/auth.guard';
import { ContactComponent } from './contact/contact.component';
import { PaymentComponent } from './payment/payment.component';
import { FaqComponent } from './faq/faq.component';

const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  // { path: 'dashboard', component: DashbaordComponent },
  { path: 'addcargo', component: AddcargoComponent },
  { path: 'assgin-cargo', component: AssginCargoComponent },
  { path: 'viewcargostatus', component: ViewcargostatusComponent },
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  {path: 'faq', component: FaqComponent},

  {
    path: 'dashboard/business',
    component: BusinessComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dashboard/driver',
    component: DriverComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dashboard/customer',
    component: CustomerComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'payment',
    component: PaymentComponent,
    canActivate: [AuthGuard]
  },

  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }