import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  email : string = "";
  password: string = "";

  constructor(private router: Router) {}

  onSubmit() {
    // Call to auth service to login
    console.log('Login with this email:', this.email, ' and this password:', this.password);

    this.router.navigateByUrl('marketplace');
  }
}
