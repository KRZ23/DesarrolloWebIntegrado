import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { RegistrosService, Registro } from '../services/registros.service';

@Component({
  selector: 'app-registro-formulario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './registro-formulario.html',
  styleUrls: ['./registro-formulario.css']
})
export class RegistroFormularioComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  editMode = false;
  id: number | null = null;

  constructor(private fb: FormBuilder, private svc: RegistrosService, private router: Router, private route: ActivatedRoute) {
    this.form = this.fb.group({
      tipoImpuesto: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      monto: [0, [Validators.required, Validators.min(0.01)]],
      fechaVencimiento: ['', Validators.required],
      estado: ['PENDIENTE', Validators.required]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const editFlag = this.route.snapshot.url.some(s => s.path === 'editar');
    if (idParam && editFlag) {
      this.editMode = true;
      this.id = Number(idParam);
      this.svc.obtener(this.id).subscribe({
        next: r => this.form.patchValue(r),
        error: e => console.error(e)
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const value: Registro = this.form.value as Registro;
    this.loading = true;
    const obs = this.editMode && this.id ? this.svc.actualizar(this.id, value) : this.svc.crear(value);
    obs.subscribe({
      next: () => { this.loading = false; this.router.navigate(['/registros']); },
      error: e => { console.error(e); this.loading = false; }
    });
  }

  volver() { this.router.navigate(['/registros']); }
}
