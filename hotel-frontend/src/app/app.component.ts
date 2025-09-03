import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AppKeycloakService } from './keycloak/services/appKeycloakService';
import { UserService } from './services/user.service';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  constructor(private userService:UserService,private appkeycloakService:AppKeycloakService){}
ngOnInit(): void {
// console.log("app component user profile" ,this.appkeycloakService.profile);
// console.log('token:'+this.appkeycloakService.profile?.token);
 
}
}