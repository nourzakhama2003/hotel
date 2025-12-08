import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  hotels?: HotelData[];  // Add hotel data
}

export interface HotelData {
  name?: string;
  location?: string;
  description?: string;
  price?: string;
  rating?: string;
  amenities?: string;
  image_url?: string;
}

export interface ChatResponse {
  response: string;
  hotels?: HotelData[];  // Add hotel data
  external_search?: boolean;  // Flag for external search
  confidence?: number;
  timestamp: string;
}export interface RecommendationRequest {
  location?: string;
  max_price?: number;
  min_rating?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  // Direct connection to Flask chatbot service
  private apiUrl = environment.CHATBOT_URL;

  constructor(private http: HttpClient) { }

  /**
   * Send a message to the chatbot
   */
  sendMessage(message: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.apiUrl}/chat`, { message });
  }

  /**
   * Get hotel recommendations based on criteria
   */
  getRecommendations(criteria: RecommendationRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.apiUrl}/recommend`, criteria);
  }

  /**
   * Get list of available destinations
   */
  getDestinations(): Observable<{ destinations: string[]; count: number }> {
    return this.http.get<{ destinations: string[]; count: number }>(`${this.apiUrl}/destinations`);
  }
}
