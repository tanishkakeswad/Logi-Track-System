import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';


@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent {

itemForm: FormGroup;  
  formModel: any = { role: null, email: '', password: '', username: '' };
  showMessage: boolean = false;
  errorMessage: boolean = false;
  responseMessage: any;
  passwordFieldType: string = 'password';
  constructor(public router: Router, private httpService: HttpService, private formBuilder: FormBuilder) {
    //form validators for all the fields
    this.itemForm = this.formBuilder.group({
      username: [this.formModel.username, [Validators.required, Validators.pattern("^[a-z]\\w{5,19}$")]],
      email: [this.formModel.email, [Validators.required, Validators.email]],
      password: [this.formModel.password, [Validators.required, Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&].{8,}$")]],
      retypepassword: ['', [Validators.required]],
      role: [this.formModel.role, [Validators.required]]

    },
      {
        validator: this.matchPassword
      }
    );
  }
  ngOnInit(): void {
  }

  //custom validation Retype Password should be as same as the password enter
  matchPassword(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const retypepassword = control.get('retypepassword');
    if (password?.value === retypepassword?.value) {
      return null;
    } else {
      return { notMatch: true };
    }
  }

  //show Password functionality
  togglePasswordVisibility() {
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  //functon for registering the user into the database through httpService
  onRegister() {
    if (this.itemForm.valid) {
      this.showMessage = false;
      this.httpService.registerUser(this.itemForm.value).subscribe(data => {
        this.showMessage = true;
        this.responseMessage = 'Welcome ' + data.name + " you are successfully registered";
        this.itemForm.reset();
        setTimeout(() => {
          this.router.navigateByUrl('/login')
        }, 2000);

      }, error => {
        this.errorMessage = true;
        this.responseMessage = 'Username already exists';
      })
    }
    else {
      this.itemForm.markAllAsTouched();
    }
  }

}
