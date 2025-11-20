// src/app/auth/login/login.ts
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { TokenService } from '../../core/services/token.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'] 
})
export class LoginComponent {
  form!: FormGroup;
  loading = false;
  errorMsg: string | null = null;

  constructor(private authService: AuthService, private router: Router, private fb: FormBuilder, private tokenSvc: TokenService) {
    // Sincronizar nombres de controles con la plantilla (rut10, password)
    this.form = this.fb.group({
      rut10: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10)]],
      password: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const { rut10, password } = this.form.value as { rut10: string; password: string };
    this.loading = true;
    this.errorMsg = null;
    this.authService.login(rut10, password).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.accessToken);
        this.loading = false;
        const tipoPersona = this.tokenSvc.getClaim<string>('tipoPersona');
        // Redirección condicional básica según claim
        if (tipoPersona === 'JURIDICA') {
          this.router.navigate(['/inicio']);
        } else {
          this.router.navigate(['/inicio']);
        }
      },
      error: () => {
        this.errorMsg = 'Credenciales incorrectas';
        this.loading = false;
      }
    });
  }
}

