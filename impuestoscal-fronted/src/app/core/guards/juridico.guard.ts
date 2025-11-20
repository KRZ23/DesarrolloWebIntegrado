import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

// Permite acceso solo a roles USUARIO_JURIDICO o ADMIN
export const juridicoGuard: CanActivateFn = () => {
  const router = inject(Router);
  const tokenSvc = inject(TokenService);
  const roles = tokenSvc.getRoles();
  if (roles.includes('USUARIO_JURIDICO') || roles.includes('ADMIN')) {
    return true;
  }
  router.navigate(['/inicio']);
  return false;
};
