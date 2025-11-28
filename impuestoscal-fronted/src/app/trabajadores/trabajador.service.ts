import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Trabajador, TrabajadorCreate, TrabajadorUpdate, ResumenPlanilla } from './trabajador.models';

@Injectable({ providedIn: 'root' })
export class TrabajadorService {
  private base = `${environment.apiBaseUrl}/trabajadores`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Trabajador[]> { return this.http.get<Trabajador[]>(this.base); }
  obtener(id: number): Observable<Trabajador> { return this.http.get<Trabajador>(`${this.base}/${id}`); }
  crear(payload: TrabajadorCreate): Observable<Trabajador> { return this.http.post<Trabajador>(this.base, payload); }
  actualizar(id: number, payload: TrabajadorUpdate): Observable<Trabajador> { return this.http.put<Trabajador>(`${this.base}/${id}`, payload); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/${id}`); }
  resumenPlanilla(mes: number, anio: number): Observable<ResumenPlanilla> {
    const params = new HttpParams().set('mes', String(mes)).set('anio', String(anio));
    return this.http.get<ResumenPlanilla>(`${this.base}/resumen-planilla`, { params });
  }

  generarPlanillaPdf(): Observable<Blob> {
    return this.http.get(`${this.base}/planilla-pdf`, { responseType: 'blob' });
  }
}
