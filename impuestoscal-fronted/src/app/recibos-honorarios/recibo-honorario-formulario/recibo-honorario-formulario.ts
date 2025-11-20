import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { RecibosHonorariosService } from '../recibos-honorarios.service';
import { ReciboHonorarioCreate, ReciboHonorarioUpdate, ReciboHonorario } from '../recibos-honorarios.models';

@Component({
  selector: 'app-recibo-honorario-formulario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './recibo-honorario-formulario.html'
})
export class ReciboHonorarioFormularioComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  saving = false;
  error: string | null = null;
  editId: number | null = null;

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private router: Router, private svc: RecibosHonorariosService) {
    this.form = this.fb.group({
      numeroRecibo: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\-]{3,20}$/)]],
      fechaEmision: ['', Validators.required],
      montoTotal: [0, [Validators.required, Validators.min(0.01)]],
      descripcionServicio: ['', [Validators.maxLength(250)]],
      clienteRazonSocial: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
      clienteRuc: ['', [Validators.pattern(/^\d{11}$/)]]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editId = +idParam;
      this.cargar();
    }
  }

  cargar(): void {
    if (!this.editId) return;
    this.loading = true;
    this.svc.obtener(this.editId).subscribe({
      next: r => {
        this.loading = false;
        this.form.patchValue({
          numeroRecibo: r.numeroRecibo,
          fechaEmision: r.fechaEmision,
          montoTotal: r.montoTotal,
          descripcionServicio: r.descripcionServicio,
          clienteRazonSocial: r.clienteRazonSocial,
          clienteRuc: r.clienteRuc
        });
        // deshabilitar número de recibo en edición
        this.form.get('numeroRecibo')?.disable();
      },
      error: err => { console.error(err); this.error = 'No se pudo cargar'; this.loading = false; }
    });
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    this.error = null;
    if (this.editId) {
      const payload: ReciboHonorarioUpdate = {
        fechaEmision: this.form.get('fechaEmision')?.value,
        montoTotal: this.form.get('montoTotal')?.value,
        descripcionServicio: this.form.get('descripcionServicio')?.value,
        clienteRazonSocial: this.form.get('clienteRazonSocial')?.value,
        clienteRuc: this.form.get('clienteRuc')?.value
      };
      this.svc.actualizar(this.editId, payload).subscribe({
        next: () => { this.saving = false; this.router.navigate(['/recibos-honorarios']); },
        error: err => { console.error(err); this.error = err?.error?.message || 'Error actualizando'; this.saving = false; }
      });
    } else {
      const payload: ReciboHonorarioCreate = {
        numeroRecibo: this.form.get('numeroRecibo')?.value,
        fechaEmision: this.form.get('fechaEmision')?.value,
        montoTotal: this.form.get('montoTotal')?.value,
        descripcionServicio: this.form.get('descripcionServicio')?.value,
        clienteRazonSocial: this.form.get('clienteRazonSocial')?.value,
        clienteRuc: this.form.get('clienteRuc')?.value
      };
      this.svc.crear(payload).subscribe({
        next: () => { this.saving = false; this.router.navigate(['/recibos-honorarios']); },
        error: err => { console.error(err); this.error = err?.error?.message || 'Error creando'; this.saving = false; }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/recibos-honorarios']);
  }
}
