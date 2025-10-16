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
import { DashboardService, DashboardResumen, RegistroResp, RegistroCreate } from '../dashboard.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

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

  registros: RegistroResp[] = [];
  proximos: RegistroResp[] = [];

  loadingResumen = false;
  loadingRegistros = false;
  error: string | null = null;

  // quick create form
  nuevo: RegistroCreate = { tipoImpuesto: '', monto: 0, fechaVencimiento: '' };
  creando = false;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.cargarResumen();
    this.cargarRegistros();
    this.cargarProximos(7);
  }

  cargarResumen(): void {
    this.loadingResumen = true;
    this.error = null;
    this.dashboardService.getResumen().subscribe({
      next: (data: DashboardResumen) => {
        this.total = data.total;
        this.pendientes = data.pendientes;
        this.vencidos = data.vencidos;
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

  cargarProximos(dias = 7): void {
    this.dashboardService.proximosVencimientos(dias).subscribe({
      next: (data) => this.proximos = data,
      error: (err) => console.error('Error próximos vencimientos', err)
    });
  }

  crearRegistro(): void {
    if (!this.nuevo.tipoImpuesto || !this.nuevo.fechaVencimiento || !this.nuevo.monto) {
      alert('Completa tipo, monto y fecha de vencimiento.');
      return;
    }
    this.creando = true;
    this.dashboardService.crearRegistro(this.nuevo).subscribe({
      next: () => {
        this.creando = false;
        this.nuevo = { tipoImpuesto: '', monto: 0, fechaVencimiento: '' };
        this.cargarResumen();
        this.cargarRegistros();
        this.cargarProximos(7);
      },
      error: (err) => {
        console.error('Error crear registro', err);
        alert(err?.error?.message || 'Error al crear registro');
        this.creando = false;
      }
    });
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar registro?')) return;
    this.dashboardService.eliminarRegistro(id).subscribe({
      next: () => { this.cargarResumen(); this.cargarRegistros(); },
      error: (err) => { console.error(err); alert('Error al eliminar'); }
    });
  }
}
