export interface Trabajador {
  id: number;
  dni: string;
  nombreCompleto: string;
  nombres: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  fechaIngreso: string; // ISO date
  fechaCese?: string; // ISO date
  sueldoBruto: number;
  regimenPensionario: string; // enum textual
  afpNombre?: string;
  aportePensionario?: number;
  retencionQuintaCategoria?: number;
  sueldoNeto?: number;
  activo?: boolean;
}

export interface TrabajadorCreate {
  dni: string;
  nombres: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  fechaIngreso: string;
  sueldoBruto: number;
  regimenPensionario: string;
  afpNombre?: string;
}

export interface TrabajadorUpdate {
  nombres?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  fechaCese?: string;
  sueldoBruto?: number;
  regimenPensionario?: string;
  afpNombre?: string;
}

export interface ResumenPlanilla {
  mes: number;
  anio: number;
  totalTrabajadores: number;
  totalSueldosBrutos: number;
  totalAportesPensionarios: number;
  totalRetenciones: number;
  totalSueldosNetos: number;
  essaludEmpleador: number;
}
