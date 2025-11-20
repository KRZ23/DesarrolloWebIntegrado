import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ReciboHonorario, ReciboHonorarioCreate, ReciboHonorarioUpdate } from './recibos-honorarios.models';

@Injectable({ providedIn: 'root' })
export class RecibosHonorariosService {
  private base = `${environment.apiBaseUrl}/recibos-honorarios`;

  constructor(private http: HttpClient) {}

  listar(): Observable<ReciboHonorario[]> {
    return this.http.get<ReciboHonorario[]>(this.base);
  }
  obtener(id: number): Observable<ReciboHonorario> {
    return this.http.get<ReciboHonorario>(`${this.base}/${id}`);
  }
  crear(payload: ReciboHonorarioCreate): Observable<ReciboHonorario> {
    return this.http.post<ReciboHonorario>(this.base, payload);
  }
  actualizar(id: number, payload: ReciboHonorarioUpdate): Observable<ReciboHonorario> {
    return this.http.put<ReciboHonorario>(`${this.base}/${id}`, payload);
  }
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
  listarPorPeriodo(inicio: string, fin: string): Observable<ReciboHonorario[]> {
    const params = new HttpParams().set('fechaInicio', inicio).set('fechaFin', fin);
    return this.http.get<ReciboHonorario[]>(`${this.base}/periodo`, { params });
  }
  retencionAnual(anio: number): Observable<number> {
    return this.http.get<number>(`${this.base}/retencion-anual?anio=${anio}`);
  }
  ingresosAnuales(anio: number): Observable<number> {
    return this.http.get<number>(`${this.base}/ingresos-anuales?anio=${anio}`);
  }
}
