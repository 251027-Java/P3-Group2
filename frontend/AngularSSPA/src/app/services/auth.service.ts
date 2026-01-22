import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

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
  private _authHeader?: string;

  constructor(private http: HttpClient) {}

  login(email:string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('${environment.apiUrl}/api/auth/login', {
      email, password
    });
  }

  authenticateUser(email: string, password: string): void {
    this._isAuthenticated = true;
    //
  }
}
