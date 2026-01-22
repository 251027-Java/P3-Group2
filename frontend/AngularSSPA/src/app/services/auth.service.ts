import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

interface AuthResponse{
  token: string,
  expiresIn: number,
  username: string
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _isAuthenticated = false;

  constructor(private http: HttpClient) {}

  login(email:string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/api/auth/login`, {
      email, password
    });
  }

  register(email: string, username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/api/auth/register`, {
      email, username, password
    });
  }

  setAuthentication(): void {
    this._isAuthenticated = true;
  }

  getAuthentication(): void {
    // return value of is Auth.
  }
}
