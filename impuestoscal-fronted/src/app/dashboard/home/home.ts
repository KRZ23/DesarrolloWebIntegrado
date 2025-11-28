import { Component, OnInit } from '@angular/core';
import { DashboardService, DashboardResumen, RegistroResp, RegistroCreate } from '../dashboard.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { TokenService } from '../../core/services/token.service';
import { OperacionIGVService } from '../../operacion-igv/operacion-igv.service';
import { ResumenIGV } from '../../operacion-igv/operacion-igv.models';

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule]
})
export class Home implements OnInit {

  total = 0;
  pendientes = 0;
  vencidos = 0;
  pagados = 0;

  registros: RegistroResp[] = [];
  proximos: RegistroResp[] = [];
  loadingResumen = false;
  loadingRegistros = false;

  // Creación rápida
  nuevo: RegistroCreate = {
    tipoImpuesto: '',
    monto: 0,
    fechaVencimiento: ''
  };
  creando = false;

  // Edición
  editId: number | null = null;
  editModel: any = {};
  editLoading = false;

  // Permisos (ejemplo simple)
  canIGV = false;
  canRecibos = false;
  canTrabajadores = false;

  constructor(
    private dashboardService: DashboardService,
    private auth: AuthService,
    private router: Router,
    private tokenSvc: TokenService,
    private igvService: OperacionIGVService
  ) { }

  ngOnInit(): void {
    this.cargarResumen();
    this.cargarRegistros();
    this.cargarProximos(7);
    this.checkPermissions();
  }

  checkPermissions(): void {
    const roles = this.tokenSvc.getRoles();
    // Lógica simple de ejemplo
    this.canIGV = roles.includes('ROLE_USUARIO_JURIDICO') || roles.includes('ROLE_ADMIN');
    this.canRecibos = roles.includes('ROLE_USUARIO_NATURAL') || roles.includes('ROLE_ADMIN');
    this.canTrabajadores = roles.includes('ROLE_USUARIO_JURIDICO') || roles.includes('ROLE_ADMIN');
  }

  cargarResumen(): void {
    this.loadingResumen = true;
    this.dashboardService.getResumen().subscribe({
      next: (data) => {
        this.total = data.total;
        this.pendientes = data.pendientes;
        this.vencidos = data.vencidos;
        this.pagados = data.pagados;
        this.loadingResumen = false;
      },
      error: (err) => {
        console.error('Error cargando dashboard', err);
        this.loadingResumen = false;
      }
    });
  }

  cargarRegistros(): void {
    this.loadingRegistros = true;
    this.dashboardService.listarRegistros().subscribe({
      next: (data) => {
        this.registros = data;
        this.loadingRegistros = false;
      },
      error: (err) => {
        console.error('Error cargando registros', err);
        this.loadingRegistros = false;
      }
    });
  }

  cargarProximos(dias: number): void {
    this.dashboardService.proximosVencimientos(dias).subscribe({
      next: (data) => this.proximos = data,
      error: (err) => console.error('Error cargando proximos', err)
    });
  }

  crearRegistro(): void {
    if (!this.nuevo.tipoImpuesto || !this.nuevo.monto || !this.nuevo.fechaVencimiento) {
      alert('Completa todos los campos');
      return;
    }
    this.creando = true;
    this.dashboardService.crearRegistro(this.nuevo).subscribe({
      next: (resp) => {
        this.creando = false;
        this.nuevo = { tipoImpuesto: '', monto: 0, fechaVencimiento: '' };
        this.cargarResumen();
        this.cargarRegistros();
        this.cargarProximos(7);
      },
      error: (err) => {
        console.error('Error creando registro', err);
        this.creando = false;
      }
    });
  }

  eliminar(id: number): void {
    if (!confirm('¿Seguro de eliminar este registro?')) return;
    this.dashboardService.eliminarRegistro(id).subscribe({
      next: () => {
        this.cargarResumen();
        this.cargarRegistros();
        this.cargarProximos(7);
      },
      error: (err) => console.error('Error eliminando', err)
    });
  }

  // --- EDICIÓN ---
  abrirEditar(r: RegistroResp): void {
    this.editId = r.id;
    // Copia para no mutar directo en tabla
    this.editModel = { ...r };
  }

  cancelarEditar(): void {
    this.editId = null;
    this.editModel = {};
    this.editLoading = false;
  }

  guardarEdicion(): void {
    if (!this.editId) return;
    if (!this.editModel.tipoImpuesto || !this.editModel.fechaVencimiento || !this.editModel.monto) {
      alert('Completa tipo, monto y fecha.');
      return;
    }
    if (this.editModel.monto <= 0) {
      alert('El monto debe ser mayor que 0.');
      return;
    }

    this.editLoading = true;
    const payload = {
      tipoImpuesto: this.editModel.tipoImpuesto,
      monto: this.editModel.monto,
      fechaVencimiento: this.editModel.fechaVencimiento,
      estado: this.editModel.estado
    };

    this.dashboardService.actualizarRegistro(this.editId, payload).subscribe({
      next: () => {
        this.editLoading = false;
        this.editId = null;
        // refrescar datos
        this.cargarResumen();
        this.cargarRegistros();
        this.cargarProximos(7);
      },
      error: (err) => {
        console.error('Error actualizando registro', err);
        alert(err?.error?.message || 'Error al actualizar');
        this.editLoading = false;
      }
    });
  }

  // --- ACCIONES RÁPIDAS: PAGO ---
  modalPagoOpen = false;
  registrosPendientes: RegistroResp[] = [];
  pagoLoading = false;

  abrirPagar(): void {
    this.modalPagoOpen = true;
    this.cargarPendientesPago();
  }

  cerrarPagar(): void {
    this.modalPagoOpen = false;
  }

  cargarPendientesPago(): void {
    this.dashboardService.listarPendientes().subscribe({
      next: (data) => this.registrosPendientes = data,
      error: (err) => console.error('Error cargando pendientes', err)
    });
  }

  pagarRegistro(r: RegistroResp): void {
    if (!confirm(`¿Confirmar pago de ${r.tipoImpuesto} por ${r.monto}?`)) return;

    this.pagoLoading = true;
    const payload = {
      tipoImpuesto: r.tipoImpuesto,
      monto: typeof r.monto === 'string' ? Number(r.monto) : r.monto,
      fechaVencimiento: r.fechaVencimiento,
      estado: 'PAGADO'
    };

    this.dashboardService.actualizarRegistro(r.id, payload).subscribe({
      next: () => {
        this.pagoLoading = false;
        alert('Pago registrado exitosamente');
        this.cargarPendientesPago(); // recargar lista
        this.cargarResumen(); // actualizar contadores
        this.cargarRegistros(); // actualizar tabla principal
        this.cargarProximos(7);
      },
      error: (err) => {
        console.error('Error al pagar', err);
        alert('Error al registrar pago');
        this.pagoLoading = false;
      }
    });
  }

  // --- ACCIONES RÁPIDAS: DECLARACIÓN ---
  modalDeclaracionOpen = false;
  declMes: number = new Date().getMonth() + 1;
  declAnio: number = new Date().getFullYear();
  resumenIGV: ResumenIGV | null = null;
  declLoading = false;

  abrirDeclaracion(): void {
    this.modalDeclaracionOpen = true;
    this.resumenIGV = null;
    this.calcularDeclaracion(); // cargar por defecto el mes actual
  }

  cerrarDeclaracion(): void {
    this.modalDeclaracionOpen = false;
  }

  calcularDeclaracion(): void {
    this.declLoading = true;
    this.igvService.resumenMensual(this.declMes, this.declAnio).subscribe({
      next: (data) => {
        this.resumenIGV = data;
        this.declLoading = false;
      },
      error: (err) => {
        console.error('Error calculando declaración', err);
        this.declLoading = false;
      }
    });
  }
}

