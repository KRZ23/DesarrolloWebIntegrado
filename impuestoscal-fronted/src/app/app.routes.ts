import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import { Home } from './dashboard/home/home';
import { DashboardLayoutComponent } from './layout/dashboard-layout';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { juridicoGuard } from './core/guards/juridico.guard';
import { naturalGuard } from './core/guards/natural.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'iniciar-sesion',
        pathMatch: 'full'
    },
    {
        path: 'iniciar-sesion',
        component: LoginComponent,
        canActivate: [guestGuard]
    },
    {
        path: 'registrarse',
        component: RegisterComponent,
        canActivate: [guestGuard]
    },
    {
        path: '',
        component: DashboardLayoutComponent,
        canActivate: [authGuard],
        children: [
            { path: 'inicio', component: Home },
            { path: 'operaciones-igv', canActivate: [juridicoGuard], loadComponent: () => import('./operacion-igv/operacion-igv').then(m => m.OperacionIGVComponent) },
            { path: 'recibos-honorarios', canActivate: [naturalGuard], loadComponent: () => import('./recibos-honorarios/recibo-honorario-lista/recibo-honorario-lista').then(m => m.ReciboHonorarioListaComponent) },
            { path: 'recibos-honorarios/nuevo', canActivate: [naturalGuard], loadComponent: () => import('./recibos-honorarios/recibo-honorario-formulario/recibo-honorario-formulario').then(m => m.ReciboHonorarioFormularioComponent) },
            { path: 'recibos-honorarios/:id', canActivate: [naturalGuard], loadComponent: () => import('./recibos-honorarios/recibo-honorario-formulario/recibo-honorario-formulario').then(m => m.ReciboHonorarioFormularioComponent) },
            { path: 'trabajadores', canActivate: [juridicoGuard], loadComponent: () => import('./trabajadores/trabajador-lista/trabajador-lista').then(m => m.TrabajadorListaComponent) },
            { path: 'trabajadores/nuevo', canActivate: [juridicoGuard], loadComponent: () => import('./trabajadores/trabajador-formulario/trabajador-formulario').then(m => m.TrabajadorFormularioComponent) },
            { path: 'trabajadores/:id', canActivate: [juridicoGuard], loadComponent: () => import('./trabajadores/trabajador-formulario/trabajador-formulario').then(m => m.TrabajadorFormularioComponent) },
            { path: 'registros', loadComponent: () => import('./features/registros/lista-registros/registro-lista').then(m => m.RegistroListaComponent) },
            { path: 'registros/nuevo', loadComponent: () => import('./features/registros/formulario-registro/registro-formulario').then(m => m.RegistroFormularioComponent) },
            { path: 'registros/:id', loadComponent: () => import('./features/registros/registro-detalle/registro-detalle').then(m => m.RegistroDetalleComponent) },
            { path: 'registros/:id/editar', loadComponent: () => import('./features/registros/formulario-registro/registro-formulario').then(m => m.RegistroFormularioComponent) }
        ]
    },
    { path: '**', redirectTo: 'inicio' }


];
