// src/app/auth/register/register.ts
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, CommonModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  form!: FormGroup;
  loading = false;
  errorMsg: string | null = null;

  constructor(private authService: AuthService, private router: Router, private fb: FormBuilder) {
    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      rut10: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      email: ['', [Validators.email]],
      telefono: [''],
      tipoCuenta: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      aceptoTerminos: [false, Validators.requiredTrue]
    });
  }

  private mapTipoCuentaToBackend(tipo: string) {
    // Asegúrate que coincida con lo que espera tu backend: "NATURAL" o "JURIDICA"
    if (!tipo) return '';
    return tipo.toLowerCase().includes('jurid') ? 'JURIDICA' : 'NATURAL';
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { nombre, rut10, tipoCuenta, password, confirmPassword } = this.form.value as any;
    if (password !== confirmPassword) {
      this.errorMsg = 'Las contraseñas no coinciden';
      return;
    }
    const payload = {
      rut10: String(rut10).padStart(10, '0').slice(0, 10),
      claveSol: password,
      tipoPersona: this.mapTipoCuentaToBackend(tipoCuenta),
      nombre
    };
    this.loading = true;
    this.errorMsg = null;
    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/iniciar-sesion']);
      },
      error: (err) => {
        this.loading = false;
        const msg = err?.error?.message || err?.error || 'Error al registrar';
        this.errorMsg = msg;
      }
    });
  }
}

