import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { OperacionIGVResp, OperacionIGVCreate, OperacionIGVUpdate, ResumenIGV } from './operacion-igv.models';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
    providedIn: 'root'
})
export class OperacionIGVService {

    private apiUrl = `${environment.apiBaseUrl}/api/operaciones-igv`;
    private isBrowser: boolean;

    constructor(private http: HttpClient, @Inject(PLATFORM_ID) platformId: Object) {
        this.isBrowser = isPlatformBrowser(platformId);
    }

    listar(): Observable<OperacionIGVResp[]> {
        if (!this.isBrowser) return of([]);
        return this.http.get<OperacionIGVResp[]>(this.apiUrl);
    }

    listarPorPeriodo(mes: number, anio: number): Observable<OperacionIGVResp[]> {
        if (!this.isBrowser) return of([]);
        return this.http.get<OperacionIGVResp[]>(`${this.apiUrl}/periodo?mes=${mes}&anio=${anio}`);
    }

    crear(op: OperacionIGVCreate): Observable<OperacionIGVResp> {
        return this.http.post<OperacionIGVResp>(this.apiUrl, op);
    }

    actualizar(id: number, op: OperacionIGVUpdate): Observable<OperacionIGVResp> {
        return this.http.put<OperacionIGVResp>(`${this.apiUrl}/${id}`, op);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    resumenMensual(mes: number, anio: number): Observable<ResumenIGV> {
        if (!this.isBrowser) return of({
            totalVentas: 0, totalCompras: 0, igvVentas: 0, igvCompras: 0, igvAPagar: 0, saldoFavor: 0,
            ventas: [], compras: []
        });
        return this.http.get<ResumenIGV>(`${this.apiUrl}/resumen-mensual?mes=${mes}&anio=${anio}`);
    }
}
