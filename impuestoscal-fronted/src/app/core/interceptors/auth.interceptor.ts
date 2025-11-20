import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let token: string | null = null;
  const router = inject(Router);

  // Verifica si está en entorno navegador antes de acceder a localStorage
  if (typeof window !== 'undefined' && window.localStorage) {
    token = window.localStorage.getItem('token');
  }

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(req).pipe(
    catchError(err => {
      if (err.status === 401) {
        if (typeof window !== 'undefined') {
          localStorage.removeItem('token');
        }
        router.navigate(['/iniciar-sesion']);
      }
      return throwError(() => err);
    })
  );
};
