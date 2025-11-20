import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

export const guestGuard: CanActivateFn = () => {
  const tokenSvc = inject(TokenService);
  const router = inject(Router);
  const token = tokenSvc.get();
  if (token && !tokenSvc.isExpired()) {
    router.navigate(['/inicio']);
    return false;
  }
  return true;
};
