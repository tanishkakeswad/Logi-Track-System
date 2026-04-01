import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';

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
  passwordFieldType:string= 'password';

  constructor(
    private router: Router,
    private httpService: HttpService,
    private fb: FormBuilder
  ) {

    this.itemForm = this.fb.group({
      username: ['', [Validators.required, Validators.pattern("^[a-zA-Z][a-zA-Z0-9]{4,18}$")]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@$!%*?&]).{8,}$")]],
      retypepassword: ['', Validators.required],
      role: [null, Validators.required]
    }, {
      validators: this.matchPassword
    });
  }

  // ✅ Password match validator
  matchPassword(control: AbstractControl): ValidationErrors | null {
    const pass = control.get('password')?.value;
    const confirm = control.get('retypepassword')?.value;

    if(pass === confirm){
      return null;
    }
    return {notMatch:true};
  }

  togglePasswordVisibility() {
    this.passwordFieldType =
      this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  // ✅ Submit Handler
  onRegister() {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    this.showMessage = false;
    this.errorMessage = false;


    this.httpService.registerUser(this.itemForm.value).subscribe(
      {
        next:(data:any)=>{
          this.showMessage=true;
          this.responseMessage = `Welcome ${data.name}, you are successfully registered`;
          this.itemForm.reset();

          setTimeout(()=>{
            this.router.navigateByUrl('/login');
          },2000);
        },
        error:(err)=>{
          console.log("Backend Error:",err);
          this.errorMessage = true;
          if(err.status===409){
            this.responseMessage = 'Username already exists';
          }else if(err.status ===400){
            this.responseMessage = 'Invalid data sent';
          }else{
            this.responseMessage = 'Something went wrong. Try again';
          }
        }
      }
    );
  }

}