import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { TokenService } from 'src/app/core/services/token.service';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
    selector: 'app-dashboard-layout',
    templateUrl: './dashboard-layout.html',
    standalone: true,
    imports: [CommonModule, RouterModule]
})
export class DashboardLayout {
    canIGV = false;
    canRecibos = false;
    canTrabajadores = false;

    constructor(private tokenSvc: TokenService, private auth: AuthService, private router: Router) {
        this.checkPermissions();
    }

    checkPermissions(): void {
        const roles = this.tokenSvc.getRoles();
        this.canIGV = roles.includes('ROLE_USUARIO_JURIDICO') || roles.includes('ROLE_ADMIN');
        this.canRecibos = roles.includes('ROLE_USUARIO_NATURAL') || roles.includes('ROLE_ADMIN');
        this.canTrabajadores = roles.includes('ROLE_USUARIO_JURIDICO') || roles.includes('ROLE_ADMIN');
    }

    logout(): void {
        this.auth.logout();
        this.router.navigate(['/iniciar-sesion']);
    }
}
