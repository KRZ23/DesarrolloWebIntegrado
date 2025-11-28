// //src/app/dashboard/home/home.ts
// import { Component, OnInit } from '@angular/core';
// import { DashboardService } from '../dashboard.service';

// @Component({
//   selector: 'app-home',
//   templateUrl: './home.html',
//   styleUrls: ['./home.css']
// })
// export class Home implements OnInit {

//   total: number = 0;
//   pendientes: number = 0;
//   vencidos: number = 0;

//   constructor(private dashboardService: DashboardService) {}

//   ngOnInit(): void {
//     this.cargarResumen();
//   }

//   cargarResumen(): void {
//     this.dashboardService.getResumen().subscribe({
//       next: (data) => {
//         this.total = data.total;
//         this.pendientes = data.pendientes;
//         this.vencidos = data.vencidos;
//       },
//       error: (err) => {
//         console.error('Error cargando dashboard:', err);
//       }
//     });
//   }
// }

// src/app/dashboard/home/home.ts
import { Component, OnInit } from '@angular/core';
import { DashboardService, DashboardResumen, RegistroResp, RegistroCreate, OperacionCreate, OperacionResp } from '../dashboard.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { TokenService } from '../../core/services/token.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home implements OnInit {
  total = 0;
  pendientes = 0;
  vencidos = 0;
  pagados = 0;
  
  // Nuevas métricas
  ventasMes = 0;
  comprasMes = 0;
  igvEstimado = 0;

  registros: RegistroResp[] = [];
  operaciones: OperacionResp[] = []; // Nueva lista
  proximos: RegistroResp[] = [];

  loadingResumen = false;
  loadingRegistros = false;
  error: string | null = null;

  // quick create form (Ahora para Operaciones)
  nuevaOperacion: OperacionCreate = { 
    tipo: 'VENTA', 
    numeroDocumento: '', 
    fechaOperacion: new Date().toISOString().split('T')[0], 
    razonSocialTercero: 'Cliente Rápido', 
    rucTercero: '00000000000', 
    baseImponible: 0, 
    descripcion: 'Registro Rápido' 
  };
  creando = false;

  canIGV = false;
  canRecibos = false;
  canTrabajadores = false;

  // Modal Declaración
  showDeclaracionModal = false;
  declaracionMes: number = new Date().getMonth() + 1;
  declaracionAnio: number = new Date().getFullYear();
  generandoPdf = false;

  // Modal Pago
  showPagoModal = false;
  pagoMonto: number = 0;
  procesandoPago = false;
  
  // Lista de items a pagar en el modal
  listaPorPagar: { id?: number; descripcion: string; monto: number; tipo: 'DEUDA' | 'IGV_MES'; selected: boolean }[] = [];
  totalSeleccionado: number = 0;

  constructor(private dashboardService: DashboardService, private auth: AuthService, private router: Router, private tokenSvc: TokenService) {}

  ngOnInit(): void {
    this.cargarResumen();
    this.cargarRegistros();
    this.cargarOperaciones(); // Cargar operaciones
    this.cargarProximos(7);
    const roles = this.tokenSvc.getRoles();
    this.canIGV = roles.includes('USUARIO_JURIDICO') || roles.includes('ADMIN');
    this.canRecibos = roles.includes('USUARIO_NATURAL') || roles.includes('ADMIN');
    this.canTrabajadores = roles.includes('USUARIO_JURIDICO') || roles.includes('ADMIN');
  }

  cargarResumen(): void {
    this.loadingResumen = true;
    this.error = null;
    this.dashboardService.getResumen().subscribe({
      next: (data: DashboardResumen) => {
        this.total = data.total;
        this.pendientes = data.pendientes;
        this.vencidos = data.vencidos;
        this.pagados = data.pagados;
        this.ventasMes = data.ventasMes || 0;
        this.comprasMes = data.comprasMes || 0;
        this.igvEstimado = data.igvEstimado || 0;
        this.loadingResumen = false;
      },
      error: (err) => {
        console.error('Error cargando dashboard:', err);
        this.error = 'No se pudo cargar el resumen';
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
        console.error('Error cargando registros:', err);
        this.error = 'No se pudo cargar los registros';
        this.loadingRegistros = false;
      }
    });
  }

  cargarOperaciones(): void {
    this.dashboardService.listarOperaciones().subscribe({
      next: (data) => {
        // Mostrar solo las últimas 5
        this.operaciones = data.sort((a, b) => new Date(b.fechaOperacion).getTime() - new Date(a.fechaOperacion).getTime()).slice(0, 5);
      },
      error: (err) => console.error('Error cargando operaciones', err)
    });
  }

  cargarProximos(dias = 7): void {
    this.dashboardService.proximosVencimientos(dias).subscribe({
      next: (data) => this.proximos = data,
      error: (err) => console.error('Error próximos vencimientos', err)
    });
  }

  // Método antiguo eliminado o reemplazado por crearOperacionRapida
  // crearRegistro(): void { ... }

  eliminar(id: number) {
    if (!confirm('¿Eliminar registro?')) return;
    this.dashboardService.eliminarRegistro(id).subscribe({
      next: () => { this.cargarResumen(); this.cargarRegistros(); },
      error: (err) => { console.error(err); alert('Error al eliminar'); }
    });
  }

  cambiarEstado(r: RegistroResp, nuevoEstado: string) {
    if (!confirm(`¿Cambiar estado a ${nuevoEstado}?`)) return;
    
    const payload = {
      tipoImpuesto: r.tipoImpuesto,
      monto: r.monto,
      fechaVencimiento: r.fechaVencimiento,
      estado: nuevoEstado
    };

    this.dashboardService.actualizarRegistro(r.id, payload).subscribe({
      next: () => {
        this.cargarResumen();
        this.cargarRegistros();
        this.cargarProximos(7);
      },
      error: (err) => {
        console.error('Error actualizando estado', err);
        alert('Error al actualizar estado');
      }
    });
  }

  // --- EDICIÓN: propiedades ---
  editId: number | null = null;
  editModel: { tipoImpuesto: string; monto: number; fechaVencimiento: string; estado?: string } = {
    tipoImpuesto: '',
    monto: 0,
    fechaVencimiento: ''
  };
  editLoading = false;

  // --- EDICIÓN: abrir el editor con los datos del registro ---
  abrirEditar(r: RegistroResp): void {
    this.editId = r.id;
    const montoNum = typeof r.monto === 'string' ? Number(r.monto) : r.monto;
    this.editModel = {
      tipoImpuesto: r.tipoImpuesto,
      monto: montoNum,
      fechaVencimiento: r.fechaVencimiento, // debe ser 'YYYY-MM-DD'
      estado: r.estado
    };
    // opcional: desplazar la vista al modal si existe
    setTimeout(() => {
      const el = document.getElementById('modal-editar');
      if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 0);
  }

  // --- EDICIÓN: cancelar ---
  cancelarEditar(): void {
    this.editId = null;
    this.editModel = { tipoImpuesto: '', monto: 0, fechaVencimiento: '' };
    this.editLoading = false;
  }

  // --- EDICIÓN: guardar cambios (PUT) ---
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

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/iniciar-sesion']);
  }

  abrirDeclaracionModal(): void {
    const now = new Date();
    this.declaracionMes = now.getMonth() + 1;
    this.declaracionAnio = now.getFullYear();
    this.showDeclaracionModal = true;
  }

  cerrarDeclaracionModal(): void {
    this.showDeclaracionModal = false;
  }

  presentarDeclaracion(): void {
    this.abrirDeclaracionModal();
  }

  confirmarDeclaracion() {
    this.generandoPdf = true;
    this.dashboardService.generarDeclaracionPdf(this.declaracionMes, this.declaracionAnio).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `declaracion-${this.declaracionAnio}-${this.declaracionMes}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.generandoPdf = false;
        this.cerrarDeclaracionModal();
      },
      error: (err: any) => {
        console.error('Error generando PDF', err);
        this.generandoPdf = false;
        alert('Error al generar la declaración');
      }
    });
  }

  abrirPagoModal() {
    this.showPagoModal = true;
    this.listaPorPagar = [];
    
    // 1. Agregar deudas pendientes
    this.dashboardService.listarPendientes().subscribe({
      next: (pendientes) => {
        pendientes.forEach(p => {
          this.listaPorPagar.push({
            id: p.id,
            descripcion: `${p.tipoImpuesto} (Vence: ${p.fechaVencimiento})`,
            monto: p.monto,
            tipo: 'DEUDA',
            selected: true
          });
        });
        
        // 2. Agregar IGV del mes si es positivo
        if (this.igvEstimado > 0) {
          this.listaPorPagar.push({
            descripcion: `IGV Estimado del Mes Actual`,
            monto: this.igvEstimado,
            tipo: 'IGV_MES',
            selected: true
          });
        }
        
        this.calcularTotalSeleccionado();
      },
      error: (err) => console.error('Error cargando pendientes para pago', err)
    });
  }

  calcularTotalSeleccionado() {
    this.totalSeleccionado = this.listaPorPagar
      .filter(item => item.selected)
      .reduce((sum, item) => sum + item.monto, 0);
  }

  toggleSeleccion(item: any) {
    item.selected = !item.selected;
    this.calcularTotalSeleccionado();
  }

  toggleTodos(event: any) {
    const checked = event.target.checked;
    this.listaPorPagar.forEach(item => item.selected = checked);
    this.calcularTotalSeleccionado();
  }

  get todosSeleccionados(): boolean {
    return this.listaPorPagar.length > 0 && this.listaPorPagar.every(i => i.selected);
  }

  cerrarPagoModal() {
    this.showPagoModal = false;
  }

  realizarPago() {
    const seleccionados = this.listaPorPagar.filter(i => i.selected);
    if (seleccionados.length === 0) {
      alert('Seleccione al menos un ítem para pagar.');
      return;
    }

    this.procesandoPago = true;
    
    const registroIds = seleccionados.filter(i => i.tipo === 'DEUDA').map(i => i.id!);
    const pagarIgvMes = seleccionados.some(i => i.tipo === 'IGV_MES');

    this.dashboardService.realizarPago(registroIds, pagarIgvMes).subscribe({
      next: () => {
        this.procesandoPago = false;
        this.showPagoModal = false;
        alert('Pago realizado con éxito.');
        this.cargarResumen(); // Recargar contadores
        this.cargarRegistros(); // Recargar lista
      },
      error: (err) => {
        console.error('Error al realizar pago', err);
        this.procesandoPago = false;
        alert('Error al procesar el pago');
      }
    });
  }

  crearOperacionRapida(): void {
    if (!this.nuevaOperacion.baseImponible || this.nuevaOperacion.baseImponible <= 0) {
      alert('Ingrese un monto válido');
      return;
    }
    this.creando = true;
    // Generar número de documento aleatorio para rapidez
    this.nuevaOperacion.numeroDocumento = 'RAP-' + Math.floor(Math.random() * 10000);
    
    this.dashboardService.crearOperacion(this.nuevaOperacion).subscribe({
      next: (resp) => {
        this.creando = false;
        alert('Operación registrada con éxito');
        this.nuevaOperacion.baseImponible = 0; // Reset
        this.cargarResumen(); // Actualizar contadores
        this.cargarOperaciones(); // Actualizar lista
      },
      error: (err) => {
        console.error('Error creando operación', err);
        this.creando = false;
        alert('Error al registrar operación');
      }
    });
  }

  cambiarEstadoOperacion(op: OperacionResp, nuevoEstado: string) {
    if (!confirm(`¿Marcar operación como ${nuevoEstado}?`)) return;

    this.dashboardService.actualizarOperacion(op.id, { estado: nuevoEstado }).subscribe({
      next: () => {
        this.cargarOperaciones();
        this.cargarResumen();
      },
      error: (err) => {
        console.error('Error actualizando estado de operación', err);
        alert('Error al actualizar estado');
      }
    });
  }
}
