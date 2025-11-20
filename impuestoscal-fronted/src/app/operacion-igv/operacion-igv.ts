// src/app/operaciones/operaciones-igv.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, FormsModule } from '@angular/forms';
import { OperacionIGVService } from './operacion-igv.service';
import { OperacionIGVResp, OperacionIGVCreate, OperacionIGVUpdate, TipoOperacionIGV } from './operacion-igv.models';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-operacion-igv',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
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
  form!: FormGroup;
  saving = false;

  // filtros periodo
  filtroInicio = '';
  filtroFin = '';

  // resumen mensual
  resumen: any = null;

  constructor(private svc: OperacionIGVService, private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      tipo: ['VENTA', Validators.required],
      numeroDocumento: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\-]{3,30}$/)]],
      fechaOperacion: ['', Validators.required],
      razonSocialTercero: ['', [Validators.maxLength(120)]],
      rucTercero: ['', [Validators.pattern(/^\d{11}$/)]],
      baseImponible: [0, [Validators.required, Validators.min(0.01)]],
      descripcion: ['', [Validators.maxLength(250)]]
    });
  }

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
    this.form.reset({
      tipo: 'VENTA',
      numeroDocumento: '',
      fechaOperacion: '',
      razonSocialTercero: '',
      rucTercero: '',
      baseImponible: 0,
      descripcion: ''
    });
  }

  abrirEditar(op: OperacionIGVResp): void {
    this.editing = true;
    this.editId = op.id;
    this.form.patchValue({
      tipo: op.tipo,
      numeroDocumento: op.numeroDocumento,
      fechaOperacion: op.fechaOperacion,
      razonSocialTercero: op.razonSocialTercero,
      rucTercero: op.rucTercero,
      baseImponible: op.baseImponible,
      descripcion: op.descripcion
    });
  }

  cancelar(): void {
    this.editing = false;
    this.editId = null;
    this.saving = false;
    this.form.reset({
      tipo: 'VENTA',
      numeroDocumento: '',
      fechaOperacion: '',
      razonSocialTercero: '',
      rucTercero: '',
      baseImponible: 0,
      descripcion: ''
    });
  }

  guardar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    const value = this.form.value;
    if (this.editId) {
      const payload: OperacionIGVUpdate = {
        tipo: value.tipo as TipoOperacionIGV,
        numeroDocumento: value.numeroDocumento,
        fechaOperacion: value.fechaOperacion,
        razonSocialTercero: value.razonSocialTercero,
        rucTercero: value.rucTercero,
        baseImponible: value.baseImponible,
        descripcion: value.descripcion
      };
      this.svc.actualizar(this.editId, payload).subscribe({
        next: () => { this.saving = false; this.cancelar(); this.loadAll(); },
        error: e => { console.error(e); alert('Error al actualizar'); this.saving = false; }
      });
    } else {
      const payload: OperacionIGVCreate = {
        tipo: value.tipo as TipoOperacionIGV,
        numeroDocumento: value.numeroDocumento,
        fechaOperacion: value.fechaOperacion,
        razonSocialTercero: value.razonSocialTercero,
        rucTercero: value.rucTercero,
        baseImponible: value.baseImponible,
        descripcion: value.descripcion
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

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/iniciar-sesion']);
  }
}

