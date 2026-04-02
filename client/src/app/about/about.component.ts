import { Component } from '@angular/core';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss']
})
export class AboutComponent {

  teamMembers = [
    { name: 'Tanishka Keswad', role: 'Team Lead' },
    { name: 'Shivam Korade', role: 'Associate Team Lead' },
    { name: 'Subham Patro', role: 'Senior Software Developer' },
    { name: 'Adithya Purama', role: 'Associate Backend Engineer' },
    { name: 'Tanmay Dhumne', role: 'Software Developer' }
  ];

}