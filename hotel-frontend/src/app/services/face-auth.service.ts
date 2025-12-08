import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FaceAuthResponse {
  success: boolean;
  message?: string;
  userId?: number;
  userName?: string;
  email?: string;
  matchScore?: number;
  faceAuthEnabled?: boolean;
  access_token?: string;
  token_type?: string;
  expires_in?: number;
  refresh_token?: string;
  tokenSource?: string;
}

@Injectable({
  providedIn: 'root'
})
export class FaceAuthService {
  private apiUrl = `${environment.FACE_RECOGNITION_API_URL}`;

  constructor(private http: HttpClient) { }

  /**
   * Register a user's face for face authentication
   */
  registerFace(userId: number, faceImage: Blob): Observable<FaceAuthResponse> {
    const formData = new FormData();
    formData.append('faceImage', faceImage, 'face.jpg');

    return this.http.post<FaceAuthResponse>(
      `${this.apiUrl}/register/${userId}`,
      formData
    );
  }

  /**
   * Authenticate user using face recognition
   */
  authenticateByFace(faceImage: Blob): Observable<FaceAuthResponse> {
    const formData = new FormData();
    formData.append('faceImage', faceImage, 'face.jpg');

    return this.http.post<FaceAuthResponse>(
      `${this.apiUrl}/authenticate`,
      formData
    );
  }

  /**
   * Disable face authentication for a user
   */
  disableFaceAuth(userId: number): Observable<FaceAuthResponse> {
    return this.http.delete<FaceAuthResponse>(
      `${this.apiUrl}/disable/${userId}`
    );
  }

  /**
   * Check if user has face authentication enabled
   */
  getFaceAuthStatus(userId: number): Observable<FaceAuthResponse> {
    return this.http.get<FaceAuthResponse>(
      `${this.apiUrl}/status/${userId}`
    );
  }
}
