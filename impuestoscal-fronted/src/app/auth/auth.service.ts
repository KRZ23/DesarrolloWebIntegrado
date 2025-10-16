// //url: impuestoscal-fronted/src/app/auth/auth.service.ts
// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';

// @Injectable({ providedIn: 'root' })
// export class AuthService {
//   private apiUrl = 'http://localhost:8082/api/auth';

//   constructor(private http: HttpClient) {}

//   login(rut10: string, claveSol: string): Observable<any> {
//   console.log('Enviando login al backend:', { rut10, claveSol }); 
//   return this.http.post(
//     `${this.apiUrl}/login`,
//     { rut10, claveSol },
//     { headers: { 'Content-Type': 'application/json' } }
//   );
// }

// }

// src/app/auth/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8082/api/auth';

  constructor(private http: HttpClient) {}

  login(rut10: string, claveSol: string): Observable<any> {
    console.log('Enviando login al backend:', { rut10, claveSol });
    return this.http.post(`${this.apiUrl}/login`, { rut10, claveSol }, { headers: { 'Content-Type': 'application/json' }});
  }

  register(payload: { rut10: string; claveSol: string; tipoPersona: string; nombre: string }): Observable<any> {
    console.log('Enviando registro al backend:', payload);
    return this.http.post(`${this.apiUrl}/register`, payload, { headers: { 'Content-Type': 'application/json' }});
  }
}




