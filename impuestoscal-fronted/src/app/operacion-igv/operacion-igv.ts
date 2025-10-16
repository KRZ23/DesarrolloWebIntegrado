// src/app/operaciones/operaciones-igv.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OperacionIGVService } from './operacion-igv.service';
import { OperacionIGVResp, OperacionIGVCreate, OperacionIGVUpdate, TipoOperacionIGV } from './operacion-igv.models';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-operacion-igv',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './operacion-igv.html',
  styleUrls: ['./operacion-igv.css']
})
export class OperacionIGVComponent implements OnInit {
  operaciones: OperacionIGVResp[] = [];
  loading = false;
  error: string | null = null;

  // estados modal crear/editar
  editing = false;
  editId: number | null = null;
  model: Partial<OperacionIGVCreate & OperacionIGVUpdate> = {
    tipo: 'VENTA',
    numeroDocumento: '',
    fechaOperacion: '',
    razonSocialTercero: '',
    rucTercero: '',
    baseImponible: 0,
    descripcion: ''
  };
  saving = false;

  // filtros periodo
  filtroInicio = '';
  filtroFin = '';

  // resumen mensual
  resumen: any = null;

  constructor(private svc: OperacionIGVService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.error = null;
    this.svc.listar().subscribe({
      next: res => { this.operaciones = res; this.loading = false; },
      error: err => { console.error(err); this.error = 'No se pudo cargar operaciones'; this.loading = false; }
    });
  }

  abrirCrear(): void {
    this.editing = true;
    this.editId = null;
    this.model = { tipo: 'VENTA', numeroDocumento: '', fechaOperacion: '', razonSocialTercero: '', baseImponible: 0, descripcion: '' };
  }

  abrirEditar(op: OperacionIGVResp): void {
    this.editing = true;
    this.editId = op.id;
    this.model = {
      tipo: op.tipo,
      numeroDocumento: op.numeroDocumento,
      fechaOperacion: op.fechaOperacion,
      razonSocialTercero: op.razonSocialTercero,
      rucTercero: op.rucTercero,
      baseImponible: op.baseImponible,
      descripcion: op.descripcion
    };
  }

  cancelar(): void {
    this.editing = false;
    this.editId = null;
    this.saving = false;
  }

  guardar(): void {
    // validaciones simples
    if (!this.model.tipo || !this.model.numeroDocumento || !this.model.fechaOperacion || !this.model.baseImponible) {
      alert('Completa tipo, número, fecha y base imponible');
      return;
    }
    this.saving = true;
    if (this.editId) {
      const payload: OperacionIGVUpdate = {
        tipo: this.model.tipo as TipoOperacionIGV,
        numeroDocumento: this.model.numeroDocumento,
        fechaOperacion: this.model.fechaOperacion,
        razonSocialTercero: this.model.razonSocialTercero,
        rucTercero: this.model.rucTercero,
        baseImponible: this.model.baseImponible,
        descripcion: this.model.descripcion
      };
      this.svc.actualizar(this.editId, payload).subscribe({
        next: () => { this.saving = false; this.cancelar(); this.loadAll(); },
        error: e => { console.error(e); alert('Error al actualizar'); this.saving = false; }
      });
    } else {
      const payload: OperacionIGVCreate = {
        tipo: this.model.tipo as TipoOperacionIGV,
        numeroDocumento: this.model.numeroDocumento!,
        fechaOperacion: this.model.fechaOperacion!,
        razonSocialTercero: this.model.razonSocialTercero!,
        rucTercero: this.model.rucTercero,
        baseImponible: this.model.baseImponible!,
        descripcion: this.model.descripcion
      };
      this.svc.crear(payload).subscribe({
        next: () => { this.saving = false; this.cancelar(); this.loadAll(); },
        error: e => { console.error(e); alert('Error al crear'); this.saving = false; }
      });
    }
  }

  eliminar(id: number) {
    if (!confirm('Eliminar operación?')) return;
    this.svc.eliminar(id).subscribe({
      next: () => this.loadAll(),
      error: e => { console.error(e); alert('Error al eliminar'); }
    });
  }

  buscarPeriodo() {
    if (!this.filtroInicio || !this.filtroFin) { alert('Selecciona rango'); return; }
    this.svc.listarPorPeriodo(this.filtroInicio, this.filtroFin).subscribe({
      next: res => this.operaciones = res,
      error: e => { console.error(e); alert('Error filtrando'); }
    });
  }

  calcularResumen(mes: number, anio: number) {
    this.svc.resumenMensual(mes, anio).subscribe({
      next: r => this.resumen = r,
      error: e => { console.error(e); alert('Error calculando resumen'); }
    });
  }
}

