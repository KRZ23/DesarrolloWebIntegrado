import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  rut10 = '';
  claveSol = '';

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.authService.login(this.rut10, this.claveSol).subscribe({
      next: (res) => {
        // Guardar token y roles
        localStorage.setItem('token', res.accessToken);
        localStorage.setItem('roles', JSON.stringify(res.roles));
        this.router.navigate(['/inicio']);
      },
      error: () => {
        alert('Credenciales incorrectas');
      }
    });
  }
}

