import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dahsboard-ngo',
  imports: [],
  templateUrl: './dahsboard-ngo.component.html',
  styleUrl: './dahsboard-ngo.component.css'
})
export class DahsboardNgoComponent {

  private router = inject(Router);

  logout(){
    localStorage.removeItem('econexo_token');
    this.router.navigate(['/login']);
  }
}
