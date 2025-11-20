import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { RecibosHonorariosService } from '../recibos-honorarios.service';
import { ReciboHonorario } from '../recibos-honorarios.models';

@Component({
  selector: 'app-recibo-honorario-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './recibo-honorario-lista.html'
})
export class ReciboHonorarioListaComponent implements OnInit {
  recibos: ReciboHonorario[] = [];
  loading = false;
  error: string | null = null;

  filtroInicio = '';
  filtroFin = '';

  resumenRetencion: number | null = null;
  resumenIngresos: number | null = null;
  anioResumen = new Date().getFullYear();
  cargandoResumen = false;

  constructor(private svc: RecibosHonorariosService, private router: Router) {}

  ngOnInit(): void {
    this.cargar();
    this.calcularAnuales();
  }

  cargar(): void {
    this.loading = true;
    this.error = null;
    this.svc.listar().subscribe({
      next: res => { this.recibos = res; this.loading = false; },
      error: err => { console.error(err); this.error = 'No se pudieron cargar los recibos'; this.loading = false; }
    });
  }

  filtrarPeriodo(): void {
    if (!this.filtroInicio || !this.filtroFin) { return; }
    this.loading = true;
    this.svc.listarPorPeriodo(this.filtroInicio, this.filtroFin).subscribe({
      next: res => { this.recibos = res; this.loading = false; },
      error: err => { console.error(err); this.error = 'Error filtrando periodo'; this.loading = false; }
    });
  }

  limpiarFiltro(): void {
    this.filtroInicio = '';
    this.filtroFin = '';
    this.cargar();
  }

  nuevo(): void { this.router.navigate(['/recibos-honorarios/nuevo']); }
  editar(r: ReciboHonorario): void { this.router.navigate(['/recibos-honorarios', r.id]); }

  eliminar(r: ReciboHonorario): void {
    if (!confirm('Eliminar recibo?')) return;
    this.svc.eliminar(r.id).subscribe({
      next: () => this.cargar(),
      error: err => { console.error(err); this.error = 'Error eliminando'; }
    });
  }

  calcularAnuales(): void {
    this.cargandoResumen = true;
    this.resumenRetencion = null;
    this.resumenIngresos = null;
    this.svc.retencionAnual(this.anioResumen).subscribe({
      next: ret => { this.resumenRetencion = ret; },
      error: e => console.error(e)
    });
    this.svc.ingresosAnuales(this.anioResumen).subscribe({
      next: ing => { this.resumenIngresos = ing; this.cargandoResumen = false; },
      error: e => { console.error(e); this.cargandoResumen = false; }
    });
  }
}
