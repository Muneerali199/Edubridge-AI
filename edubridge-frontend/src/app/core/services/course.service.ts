import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Course, Instructor, VoiceRequest, VoiceResponse, VoiceOption } from '../models/course.model';
import { ApiResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = environment.courseApiUrl;

  // Course endpoints
  getCourses(page: number = 0, size: number = 12): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(`${this.API_URL}`, { params });
  }

  getCourseById(id: string): Observable<ApiResponse<Course>> {
    return this.http.get<ApiResponse<Course>>(`${this.API_URL}/${id}`);
  }

  searchCourses(keyword: string, page: number = 0, size: number = 12): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(`${this.API_URL}/search`, { params });
  }

  getCoursesByInstructor(instructorId: string, page: number = 0): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', '10');
    return this.http.get<ApiResponse<any>>(`${this.API_URL}/instructor/${instructorId}`, { params });
  }

  // Instructor endpoints
  getInstructors(page: number = 0, size: number = 12): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(`${this.API_URL}/instructors`, { params });
  }

  getInstructorById(id: string): Observable<ApiResponse<Instructor>> {
    return this.http.get<ApiResponse<Instructor>>(`${this.API_URL}/instructors/${id}`);
  }

  getTopInstructors(limit: number = 10): Observable<ApiResponse<Instructor[]>> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<ApiResponse<Instructor[]>>(`${this.API_URL}/instructors/top`, { params });
  }

  // Voice endpoints
  textToSpeech(request: VoiceRequest): Observable<ApiResponse<VoiceResponse>> {
    return this.http.post<ApiResponse<VoiceResponse>>(`${this.API_URL}/voice/text-to-speech`, request);
  }

  speechToText(audioFile: File, languageCode: string = 'en-US'): Observable<ApiResponse<VoiceResponse>> {
    const formData = new FormData();
    formData.append('audio', audioFile);
    formData.append('languageCode', languageCode);
    return this.http.post<ApiResponse<VoiceResponse>>(`${this.API_URL}/voice/speech-to-text`, formData);
  }

  getAvailableVoices(languageCode: string = 'en-US'): Observable<ApiResponse<VoiceOption[]>> {
    const params = new HttpParams().set('languageCode', languageCode);
    return this.http.get<ApiResponse<VoiceOption[]>>(`${this.API_URL}/voice/voices`, { params });
  }
}
