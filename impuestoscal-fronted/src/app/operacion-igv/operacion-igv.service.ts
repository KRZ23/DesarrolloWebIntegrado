// src/app/operaciones/operacion-igv.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { OperacionIGVResp, OperacionIGVCreate, OperacionIGVUpdate, ResumenIGV } from './operacion-igv.models';

@Injectable({ providedIn: 'root' })
export class OperacionIGVService {
  private base = `${environment.apiBaseUrl}/operaciones-igv`;

  constructor(private http: HttpClient) {}

  listar(): Observable<OperacionIGVResp[]> {
    return this.http.get<OperacionIGVResp[]>(this.base);
  }

  obtener(id: number): Observable<OperacionIGVResp> {
    return this.http.get<OperacionIGVResp>(`${this.base}/${id}`);
  }

  listarPorTipo(tipo: string): Observable<OperacionIGVResp[]> {
    return this.http.get<OperacionIGVResp[]>(`${this.base}/tipo/${tipo}`);
  }

  crear(payload: OperacionIGVCreate): Observable<OperacionIGVResp> {
    return this.http.post<OperacionIGVResp>(this.base, payload);
  }

  actualizar(id: number, payload: OperacionIGVUpdate): Observable<OperacionIGVResp> {
    return this.http.put<OperacionIGVResp>(`${this.base}/${id}`, payload);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  listarPorPeriodo(fechaInicio: string, fechaFin: string): Observable<OperacionIGVResp[]> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);
    return this.http.get<OperacionIGVResp[]>(`${this.base}/periodo`, { params });
  }

  resumenMensual(mes: number, anio: number): Observable<ResumenIGV> {
    const params = new HttpParams().set('mes', String(mes)).set('anio', String(anio));
    return this.http.get<ResumenIGV>(`${this.base}/resumen-mensual`, { params });
  }
}
