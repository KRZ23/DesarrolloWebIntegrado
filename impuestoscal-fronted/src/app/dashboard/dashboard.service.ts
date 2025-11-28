import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { isPlatformBrowser } from '@angular/common';

export interface DashboardResumen {
  total: number;
  pendientes: number;
  vencidos: number;
  pagados: number;
}

export interface RegistroResp {
  id: number;
  tipoImpuesto: string;
  monto: number;
  fechaVencimiento: string;
  estado: string;
}

export interface RegistroCreate {
  tipoImpuesto: string;
  monto: number;
  fechaVencimiento: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = `${environment.apiBaseUrl}/api`;
  private isBrowser: boolean;

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) platformId: Object) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  getResumen(): Observable<DashboardResumen> {
    if (!this.isBrowser) return of({ total: 0, pendientes: 0, vencidos: 0, pagados: 0 });
    return this.http.get<DashboardResumen>(`${this.apiUrl}/dashboard`);
  }

  listarRegistros(): Observable<RegistroResp[]> {
    if (!this.isBrowser) return of([]);
    return this.http.get<RegistroResp[]>(`${this.apiUrl}/registros`);
  }

  crearRegistro(data: RegistroCreate): Observable<RegistroResp> {
    return this.http.post<RegistroResp>(`${this.apiUrl}/registros`, data);
  }

  actualizarRegistro(id: number, data: any): Observable<RegistroResp> {
    return this.http.put<RegistroResp>(`${this.apiUrl}/registros/${id}`, data);
  }

  eliminarRegistro(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/registros/${id}`);
  }

  listarPendientes(): Observable<RegistroResp[]> {
    if (!this.isBrowser) return of([]);
    return this.http.get<RegistroResp[]>(`${this.apiUrl}/registros/pendientes`);
  }

  proximosVencimientos(dias: number): Observable<RegistroResp[]> {
    if (!this.isBrowser) return of([]);
    return this.http.get<RegistroResp[]>(`${this.apiUrl}/registros/proximos-vencimientos?dias=${dias}`);
  }
}
