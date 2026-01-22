import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
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
  private readonly TOKEN_KEY = 'dummy jwt';

  constructor(private http: HttpClient) {}

  login(email:string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/api/auth/login`, {
      email, password
    }).pipe(
      tap(response => {
        this.setJWT(response.token);
        this.setAuthentication(true);
      })
    );
  }

  register(email: string, username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/api/auth/register`, {
      email, username, password
    }).pipe(
      tap(response => {
        this.setJWT(response.token);
        this.setAuthentication(true);
      })
    );
  }

  setAuthentication(status: boolean): void {
    this._isAuthenticated = status;
  }

  getAuthentication(): boolean {
    return this._isAuthenticated;
  }

  private setJWT(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this._isAuthenticated = false;
  }
}
