import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpService } from '../../services/http.service';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-addcargo',
  templateUrl: './addcargo.component.html',
  styleUrls: ['./addcargo.component.scss']
})
export class AddcargoComponent {
  itemForm!:FormGroup;
  formModel:any={status:null};
  showError:boolean=false;
  errorMessage:any;
  cargList:any=[];
  assignModel:any={};
  driverList:any=[];
  showMessage:any;
  responseMessage:any;
  constructor(private router:Router,private http:HttpService,private fb:FormBuilder,private authService:AuthService){
    
  }
  ngOnInit():void{

  }

  getCargo(){

  }

  getDrivers(){

  }
  onSubmit(){

  }
  addDriver(valus:any){

  }
  assignDriver(){

  }

}

