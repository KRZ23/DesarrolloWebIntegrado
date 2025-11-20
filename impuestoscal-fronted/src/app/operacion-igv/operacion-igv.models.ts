// src/app/operaciones/operacion-igv.models.ts
export type TipoOperacionIGV = 'VENTA' | 'COMPRA';

export interface OperacionIGVResp {
  id: number;
  tipo: TipoOperacionIGV;
  numeroDocumento: string;
  fechaOperacion: string; // ISO date
  razonSocialTercero: string;
  rucTercero?: string;
  baseImponible: number;
  igv: number;
  montoTotal: number;
  descripcion?: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface OperacionIGVCreate {
  tipo: TipoOperacionIGV;
  numeroDocumento: string;
  fechaOperacion: string; // 'YYYY-MM-DD'
  razonSocialTercero: string;
  rucTercero?: string;
  baseImponible: number;
  descripcion?: string;
}

export interface OperacionIGVUpdate {
  tipo?: TipoOperacionIGV;
  numeroDocumento?: string;
  fechaOperacion?: string;
  razonSocialTercero?: string;
  rucTercero?: string;
  baseImponible?: number;
  descripcion?: string;
}

export interface ResumenIGV {
  mes: number;
  anio: number;
  totalVentas: number;
  igvVentas: number;
  totalCompras: number;
  igvCompras: number;
  igvAPagar: number;
}
