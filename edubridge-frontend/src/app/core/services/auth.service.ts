import { Injectable, signal, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  ApiResponse,
  User
} from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly AUTH_URL = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'user_data';
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  // Signal for reactive components
  public isAuthenticated = signal(this.hasValidToken());

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  /**
   * Register a new user
   */
  register(request: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${this.AUTH_URL}/register`,
      request
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.handleAuthResponse(response.data);
        }
      })
    );
  }

  /**
   * Login user
   */
  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${this.AUTH_URL}/login`,
      request
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.handleAuthResponse(response.data);
        }
      })
    );
  }

  /**
   * Logout user
   */
  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.REFRESH_TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.currentUserSubject.next(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/auth/login']);
  }

  /**
   * Refresh access token
   */
  refreshToken(): Observable<ApiResponse<AuthResponse>> {
    const refreshToken = this.getRefreshToken();
    return this.http.post<ApiResponse<AuthResponse>>(
      `${this.AUTH_URL}/refresh`,
      { refreshToken }
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setToken(response.data.accessToken);
        }
      })
    );
  }

  /**
   * Handle authentication response
   */
  private handleAuthResponse(authResponse: AuthResponse): void {
    this.setToken(authResponse.accessToken);
    this.setRefreshToken(authResponse.refreshToken);

    const user: User = {
      id: authResponse.userId,
      email: authResponse.email,
      firstName: '',
      lastName: '',
      role: authResponse.role as any,
      active: true
    };

    this.setUser(user);
    this.currentUserSubject.next(user);
    this.isAuthenticated.set(true);
  }

  /**
   * Get access token
   */
  getToken(): string | null {
    return this.isBrowser ? localStorage.getItem(this.TOKEN_KEY) : null;
  }

  /**
   * Get refresh token
   */
  getRefreshToken(): string | null {
    return this.isBrowser ? localStorage.getItem(this.REFRESH_TOKEN_KEY) : null;
  }

  /**
   * Set access token
   */
  private setToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem(this.TOKEN_KEY, token);
    }
  }

  /**
   * Set refresh token
   */
  private setRefreshToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, token);
    }
  }

  /**
   * Set user data
   */
  private setUser(user: User): void {
    if (this.isBrowser) {
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }
  }

  /**
   * Get user from storage
   */
  private getUserFromStorage(): User | null {
    if (!this.isBrowser) {
      return null;
    }
    const userData = localStorage.getItem(this.USER_KEY);
    return userData ? JSON.parse(userData) : null;
  }

  /**
   * Check if user has valid token
   */
  private hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // Convert to milliseconds
      return Date.now() < exp;
    } catch (e) {
      return false;
    }
  }

  /**
   * Get current user
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Check if user is authenticated
   */
  isLoggedIn(): boolean {
    return this.isAuthenticated();
  }
}
