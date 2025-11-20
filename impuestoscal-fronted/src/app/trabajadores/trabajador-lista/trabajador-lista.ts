import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TrabajadorService } from '../trabajador.service';
import { Trabajador, ResumenPlanilla } from '../trabajador.models';

@Component({
  selector: 'app-trabajador-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './trabajador-lista.html'
})
export class TrabajadorListaComponent implements OnInit {
  trabajadores: Trabajador[] = [];
  loading = false;
  error: string | null = null;

  mesResumen = new Date().getMonth() + 1;
  anioResumen = new Date().getFullYear();
  resumen: ResumenPlanilla | null = null;
  cargandoResumen = false;

  constructor(private svc: TrabajadorService, private router: Router) {}

  ngOnInit(): void {
    this.cargar();
    this.calcularResumen();
  }

  cargar(): void {
    this.loading = true;
    this.error = null;
    this.svc.listar().subscribe({
      next: res => { this.trabajadores = res; this.loading = false; },
      error: err => { console.error(err); this.error = 'No se pudieron cargar'; this.loading = false; }
    });
  }

  nuevo(): void { this.router.navigate(['/trabajadores/nuevo']); }
  editar(t: Trabajador): void { this.router.navigate(['/trabajadores', t.id]); }

  eliminar(t: Trabajador): void {
    if (!confirm('¿Eliminar trabajador?')) return;
    this.svc.eliminar(t.id).subscribe({
      next: () => this.cargar(),
      error: err => { console.error(err); this.error = 'Error eliminando'; }
    });
  }

  calcularResumen(): void {
    this.cargandoResumen = true;
    this.resumen = null;
    this.svc.resumenPlanilla(this.mesResumen, this.anioResumen).subscribe({
      next: r => { this.resumen = r; this.cargandoResumen = false; },
      error: e => { console.error(e); this.cargandoResumen = false; }
    });
  }
}
