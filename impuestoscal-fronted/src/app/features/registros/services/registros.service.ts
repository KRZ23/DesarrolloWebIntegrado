import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Registro {
  id?: number;
  tipoImpuesto: string;
  monto: number;
  fechaVencimiento: string;
  estado?: string;
}

@Injectable({ providedIn: 'root' })
export class RegistrosService {
  private base = `${environment.apiBaseUrl}/registros`;
  constructor(private http: HttpClient) {}

  listar(): Observable<Registro[]> { return this.http.get<Registro[]>(this.base); }
  listarAdminTodos(): Observable<Registro[]> { return this.http.get<Registro[]>(`${this.base}/admin/todos`); }
  obtener(id: number): Observable<Registro> { return this.http.get<Registro>(`${this.base}/${id}`); }
  crear(reg: Registro): Observable<Registro> { return this.http.post<Registro>(this.base, reg); }
  actualizar(id: number, reg: Registro): Observable<Registro> { return this.http.put<Registro>(`${this.base}/${id}`, reg); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/${id}`); }
}
