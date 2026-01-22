import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [RouterLink, FormsModule, HttpClientModule],
  providers: [AuthService],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  email : string = "";
  password: string = "";

  constructor(private router: Router, private auth: AuthService) {}

  onSubmit() {
    // Call to auth service to login
    this.auth.login(this.email, this.password).subscribe({
      next: (response) => {
        console.log('Login success', response);
        this.router.navigateByUrl('marketplace');
      },
      error: (err) => {
        console.error('Login failed', err);
      }
    })
  }
}
