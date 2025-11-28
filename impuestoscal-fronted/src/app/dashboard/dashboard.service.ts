
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DashboardResumen { total: number; pendientes: number; vencidos: number; pagados: number; }
export interface RegistroResp { id: number; tipoImpuesto: string; monto: number; fechaVencimiento: string; estado: string; }
export interface RegistroCreate { tipoImpuesto: string; monto: number; fechaVencimiento: string; estado?: string; }

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  getResumen(): Observable<DashboardResumen> {
    return this.http.get<DashboardResumen>(`${this.base}/dashboard`);
  }

  generarDeclaracionPdf(mes: number, anio: number): Observable<Blob> {
    return this.http.get(`${this.base}/dashboard/declaracion-pdf?mes=${mes}&anio=${anio}`, { responseType: 'blob' });
  }

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
