export interface ReciboHonorario {
  id: number;
  numeroRecibo: string;
  fechaEmision: string; // ISO date
  montoTotal: number;
  retencion: number;
  montoNeto: number;
  descripcionServicio?: string;
  clienteRazonSocial: string;
  clienteRuc?: string;
  activo?: boolean;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface ReciboHonorarioCreate {
  numeroRecibo: string;
  fechaEmision: string; // ISO date
  montoTotal: number;
  descripcionServicio?: string;
  clienteRazonSocial: string;
  clienteRuc?: string;
}

export interface ReciboHonorarioUpdate {
  fechaEmision?: string;
  montoTotal?: number;
  descripcionServicio?: string;
  clienteRazonSocial?: string;
  clienteRuc?: string;
}
