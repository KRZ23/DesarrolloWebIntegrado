import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let token: string | null = null;

  // Verifica si está en entorno navegador antes de acceder a localStorage
  if (typeof window !== 'undefined' && window.localStorage) {
    token = window.localStorage.getItem('token');
  }

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(req);
};
