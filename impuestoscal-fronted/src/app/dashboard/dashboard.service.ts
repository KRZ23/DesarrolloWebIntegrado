import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = 'http://localhost:8082/api/dashboard';

  constructor(private http: HttpClient) {}

  getResumen(): Observable<any> {
    let headers = new HttpHeaders();

    // para error SSR 
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('token');
      if (token) {
        headers = headers.set('Authorization', `Bearer ${token}`);
      }
    }

    return this.http.get(this.apiUrl, { headers });
  }
}
