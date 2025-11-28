export interface OperacionIGVResp {
    id: number;
    tipoOperacion: string; // VENTA o COMPRA
    montoBase: number;
    igv: number;
    montoTotal: number;
    fechaOperacion: string;
    razonSocial: string;
    ruc: string;
}

export interface OperacionIGVCreate {
    tipoOperacion: string;
    montoBase: number;
    fechaOperacion: string;
    razonSocial: string;
    ruc: string;
}

export interface OperacionIGVUpdate {
    tipoOperacion?: string;
    montoBase?: number;
    fechaOperacion?: string;
    razonSocial?: string;
    ruc?: string;
}

export interface ResumenIGV {
    mes?: number;
    anio?: number;
    totalVentas: number;
    igvVentas: number;
    totalCompras: number;
    igvCompras: number;
    igvAPagar: number;
}
