// src/app/auth/register/register.ts
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  nombre = '';
  rut10 = '';
  email = ''; // opcional si el backend no lo usa, puedes eliminarlo
  telefono = '';
  tipoCuenta = ''; // "Persona Natural" o "Persona Jurídica" en UI
  password = '';
  confirmPassword = '';
  aceptoTerminos = false;
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  private mapTipoCuentaToBackend(tipo: string) {
    // Asegúrate que coincida con lo que espera tu backend: "NATURAL" o "JURIDICA"
    if (!tipo) return '';
    return tipo.toLowerCase().includes('jurid') ? 'JURIDICA' : 'NATURAL';
  }

  onSubmit() {
    if (!this.aceptoTerminos) {
      alert('Debes aceptar los términos');
      return;
    }
    if (this.password !== this.confirmPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }
    if (!this.rut10 || !this.password || !this.nombre) {
      alert('Completa los campos obligatorios.');
      return;
    }

    const payload = {
      rut10: String(this.rut10).padStart(10, '0').slice(0,10), 
      claveSol: this.password,
      tipoPersona: this.mapTipoCuentaToBackend(this.tipoCuenta),
      nombre: this.nombre
    };

    this.loading = true;
    this.authService.register(payload).subscribe({
      next: (res) => {
        this.loading = false;
        // éxito y  login
        alert('Registro exitoso. Inicia sesión.');
        this.router.navigate(['/iniciar-sesion']);
      },
      error: (err) => {
        this.loading = false;
        // maneja errores comunes (400 / 409)
        console.error('Error al registrar:', err);
        const msg = err?.error?.message || err?.error || 'Error al registrar';
        alert(msg);
      }
    });
  }
}

