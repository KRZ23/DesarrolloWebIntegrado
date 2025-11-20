import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TrabajadorService } from '../trabajador.service';
import { TrabajadorCreate, TrabajadorUpdate } from '../trabajador.models';

@Component({
  selector: 'app-trabajador-formulario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './trabajador-formulario.html'
})
export class TrabajadorFormularioComponent implements OnInit {
  form!: FormGroup;
  editId: number | null = null;
  loading = false;
  saving = false;
  error: string | null = null;

  regimenes = ['ONP', 'AFP']; // textual mapping

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private router: Router, private svc: TrabajadorService) {
    this.form = this.fb.group({
      dni: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      nombres: ['', [Validators.required, Validators.minLength(2)]],
      apellidoPaterno: ['', [Validators.required, Validators.minLength(2)]],
      apellidoMaterno: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.email]],
      telefono: ['', [Validators.pattern(/^\d{7,15}$/)]],
      direccion: ['', [Validators.minLength(5)]],
      fechaIngreso: ['', Validators.required],
      sueldoBruto: [0, [Validators.required, Validators.min(0.01)]],
      regimenPensionario: ['ONP', Validators.required],
      afpNombre: ['']
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editId = +idParam;
      this.cargar();
    }

    this.form.get('regimenPensionario')?.valueChanges.subscribe(val => {
      if (val === 'AFP') {
        this.form.get('afpNombre')?.setValidators([Validators.required, Validators.minLength(2)]);
      } else {
        this.form.get('afpNombre')?.clearValidators();
        this.form.get('afpNombre')?.setValue('');
      }
      this.form.get('afpNombre')?.updateValueAndValidity();
    });
  }

  cargar(): void {
    if (!this.editId) return;
    this.loading = true;
    this.svc.obtener(this.editId).subscribe({
      next: t => {
        this.loading = false;
        this.form.patchValue({
          dni: t.dni,
          nombres: t.nombres,
          apellidoPaterno: t.apellidoPaterno,
            apellidoMaterno: t.apellidoMaterno,
          email: t.email,
          telefono: t.telefono,
          direccion: t.direccion,
          fechaIngreso: t.fechaIngreso,
          sueldoBruto: t.sueldoBruto,
          regimenPensionario: t.regimenPensionario,
          afpNombre: t.afpNombre
        });
        this.form.get('dni')?.disable();
      },
      error: err => { console.error(err); this.error = 'No se pudo cargar'; this.loading = false; }
    });
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    this.error = null;
    if (this.editId) {
      const payload: TrabajadorUpdate = {
        nombres: this.form.get('nombres')?.value,
        apellidoPaterno: this.form.get('apellidoPaterno')?.value,
        apellidoMaterno: this.form.get('apellidoMaterno')?.value,
        email: this.form.get('email')?.value,
        telefono: this.form.get('telefono')?.value,
        direccion: this.form.get('direccion')?.value,
        sueldoBruto: this.form.get('sueldoBruto')?.value,
        regimenPensionario: this.form.get('regimenPensionario')?.value,
        afpNombre: this.form.get('afpNombre')?.value
      };
      this.svc.actualizar(this.editId, payload).subscribe({
        next: () => { this.saving = false; this.router.navigate(['/trabajadores']); },
        error: err => { console.error(err); this.error = err?.error?.message || 'Error actualizando'; this.saving = false; }
      });
    } else {
      const payload: TrabajadorCreate = {
        dni: this.form.get('dni')?.value,
        nombres: this.form.get('nombres')?.value,
        apellidoPaterno: this.form.get('apellidoPaterno')?.value,
        apellidoMaterno: this.form.get('apellidoMaterno')?.value,
        email: this.form.get('email')?.value,
        telefono: this.form.get('telefono')?.value,
        direccion: this.form.get('direccion')?.value,
        fechaIngreso: this.form.get('fechaIngreso')?.value,
        sueldoBruto: this.form.get('sueldoBruto')?.value,
        regimenPensionario: this.form.get('regimenPensionario')?.value,
        afpNombre: this.form.get('afpNombre')?.value
      };
      this.svc.crear(payload).subscribe({
        next: () => { this.saving = false; this.router.navigate(['/trabajadores']); },
        error: err => { console.error(err); this.error = err?.error?.message || 'Error creando'; this.saving = false; }
      });
    }
  }

  cancelar(): void { this.router.navigate(['/trabajadores']); }
}
