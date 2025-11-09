import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.model';
import { UserProfile, UpdateProfileRequest } from '../models/profile.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly API_URL = `${environment.apiUrl}/auth/profile`;

  constructor(private http: HttpClient) {}

  /**
   * Get current user's profile
   */
  getProfile(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(this.API_URL);
  }

  /**
   * Update user profile
   */
  updateProfile(request: UpdateProfileRequest): Observable<ApiResponse<UserProfile>> {
    return this.http.put<ApiResponse<UserProfile>>(this.API_URL, request);
  }
}
