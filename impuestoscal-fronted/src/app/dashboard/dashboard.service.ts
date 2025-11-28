import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DashboardResumen { 
  total: number; 
  pendientes: number; 
  vencidos: number; 
  pagados: number;
  ventasMes: number;
  comprasMes: number;
  igvEstimado: number;
}
export interface RegistroResp { id: number; tipoImpuesto: string; monto: number; fechaVencimiento: string; estado: string; }
export interface RegistroCreate { tipoImpuesto: string; monto: number; fechaVencimiento: string; estado?: string; }
export interface OperacionCreate { tipo: 'VENTA' | 'COMPRA'; numeroDocumento: string; fechaOperacion: string; razonSocialTercero: string; rucTercero: string; baseImponible: number; descripcion: string; estado?: string; }
export interface OperacionResp { id: number; tipo: string; numeroDocumento: string; fechaOperacion: string; razonSocialTercero: string; montoTotal: number; descripcion: string; estado: string; }

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

  listarOperaciones(): Observable<OperacionResp[]> {
    return this.http.get<OperacionResp[]>(`${this.base}/operaciones-igv`);
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

  crearOperacion(payload: OperacionCreate) {
    return this.http.post<any>(`${this.base}/operaciones-igv`, payload);
  }

  actualizarOperacion(id: number, payload: any) {
    return this.http.put<OperacionResp>(`${this.base}/operaciones-igv/${id}`, payload);
  }

  eliminarRegistro(id: number) {
    return this.http.delete<void>(`${this.base}/registros/${id}`);
  }

  // actualizar un registro (PUT)
  actualizarRegistro(id: number, payload: { tipoImpuesto: string; monto: number; fechaVencimiento: string; estado?: string }) {
    return this.http.put<RegistroResp>(`${this.base}/registros/${id}`, payload);
  }

  realizarPago(registroIds: number[], pagarIgvMes: boolean) {
    return this.http.post<void>(`${this.base}/dashboard/pagar`, { registroIds, pagarIgvMes });
  }
}
