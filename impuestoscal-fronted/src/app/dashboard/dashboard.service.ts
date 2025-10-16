// //src/app/dashboard/dashboard.service.ts
// import { Injectable } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { Observable } from 'rxjs';

// @Injectable({
//   providedIn: 'root'
// })
// export class DashboardService {

//   private apiUrl = 'http://localhost:8082/api/dashboard';

//   constructor(private http: HttpClient) {}

//   getResumen(): Observable<any> {
//     let headers = new HttpHeaders();

//     // para error SSR 
//     if (typeof window !== 'undefined' && window.localStorage) {
//       const token = localStorage.getItem('token');
//       if (token) {
//         headers = headers.set('Authorization', `Bearer ${token}`);
//       }
//     }

//     return this.http.get(this.apiUrl, { headers });
//   }
// }

// src/app/dashboard/dashboard.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardResumen { total: number; pendientes: number; vencidos: number; }
export interface RegistroResp { id: number; tipoImpuesto: string; monto: number; fechaVencimiento: string; estado: string; }
export interface RegistroCreate { tipoImpuesto: string; monto: number; fechaVencimiento: string; estado?: string; }

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private base = 'http://localhost:8082/api';

  constructor(private http: HttpClient) { }

  // Resumen rápido (usa /api/dashboard)
  getResumen(): Observable<DashboardResumen> {
    return this.http.get<DashboardResumen>(`${this.base}/dashboard`);
  }

  // Registros tributarios
  listarRegistros(): Observable<RegistroResp[]> {
    return this.http.get<RegistroResp[]>(`${this.base}/registros`);
  }
  listarPendientes(): Observable<RegistroResp[]> {
    return this.http.get<RegistroResp[]>(`${this.base}/registros/pendientes`);
  }
  listarVencidos(): Observable<RegistroResp[]> {
    return this.http.get<RegistroResp[]>(`${this.base}/registros/vencidos`);
  }
  proximosVencimientos(dias = 7): Observable<RegistroResp[]> {
    return this.http.get<RegistroResp[]>(`${this.base}/registros/proximos-vencimientos?dias=${dias}`);
  }

  crearRegistro(payload: RegistroCreate) {
    return this.http.post<RegistroResp>(`${this.base}/registros`, payload);
  }

  eliminarRegistro(id: number) {
    return this.http.delete<void>(`${this.base}/registros/${id}`);
  }

  // actualizar un registro (PUT)
  actualizarRegistro(id: number, payload: { tipoImpuesto: string; monto: number; fechaVencimiento: string; estado?: string }) {
    return this.http.put<RegistroResp>(`${this.base}/registros/${id}`, payload);
  }

}
