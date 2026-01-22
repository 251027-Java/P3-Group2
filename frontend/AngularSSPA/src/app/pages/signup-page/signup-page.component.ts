import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup-page',
  standalone: true,
  imports: [RouterLink, FormsModule, HttpClientModule],
  providers: [AuthService],
  templateUrl: './signup-page.component.html',
  styleUrl: './signup-page.component.css'
})
export class SignupPageComponent {
  email: string = ""
  username: string = ""
  password: string = ""

  constructor(private router: Router, private auth: AuthService) {}

  onSubmit() {
    console.log('email', this.email, 'user', this.username, 'pass', this.password);

    // call to auth service
    this.auth.register(this.email, this.username, this.password).subscribe({
      next: (response) => {
        console.log('registered')
        this.router.navigateByUrl('marketplace');
      },
      error: (err) => {
        console.error('Account creation failed', err);
      }
    })
  }
}
