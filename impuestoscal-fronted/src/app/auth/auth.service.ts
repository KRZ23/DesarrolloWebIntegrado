import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TokenService } from '../core/services/token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiBaseUrl}/api/auth`;

  constructor(private http: HttpClient, private tokenSvc: TokenService) { }

  login(rut10: string, claveSol: string): Observable<any> {
    console.log('Enviando login al backend:', { rut10, claveSol });
    return this.http.post(
      `${this.apiUrl}/login`,
      { rut10, claveSol },
      { headers: { 'Content-Type': 'application/json' } }
    );
  }

  logout(): void {
    this.tokenSvc.logOut();
  }
}
