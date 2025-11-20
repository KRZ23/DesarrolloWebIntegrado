import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TokenService } from '../core/services/token.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-layout.html'
})
export class DashboardLayoutComponent implements OnInit {
  canIGV = false;
  canRecibos = false;
  canTrabajadores = false;

  constructor(private tokenSvc: TokenService, private auth: AuthService) {}

  ngOnInit(): void {
    const roles = this.tokenSvc.getRoles();
    this.canIGV = roles.includes('ADMIN') || roles.includes('USUARIO_JURIDICO');
    this.canRecibos = roles.includes('ADMIN') || roles.includes('USUARIO_NATURAL');
    this.canTrabajadores = roles.includes('ADMIN') || roles.includes('USUARIO_JURIDICO');
  }

  logout(): void {
    this.auth.logout();
  }
}