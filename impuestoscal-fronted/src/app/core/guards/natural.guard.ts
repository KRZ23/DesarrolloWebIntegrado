import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

export const naturalGuard: CanActivateFn = () => {
  const router = inject(Router);
  const tokenSvc = inject(TokenService);
  const roles = tokenSvc.getRoles();
  if (roles.includes('USUARIO_NATURAL') || roles.includes('ADMIN')) {
    return true;
  }
  router.navigate(['/inicio']);
  return false;
};
