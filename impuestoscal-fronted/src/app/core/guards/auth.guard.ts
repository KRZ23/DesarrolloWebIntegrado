import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

export const authGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (tokenService.get() && !tokenService.isExpired()) {
    return true;
  }
  router.navigate(['/iniciar-sesion'], { queryParams: { redirect: state.url } });
  return false;
};
