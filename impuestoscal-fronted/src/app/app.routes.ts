import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import { Home } from './dashboard/home/home';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'iniciar-sesion',
        pathMatch: 'full'
    },
    {
        path: 'iniciar-sesion',
        component: LoginComponent
    },
    {
        path: 'registrarse',
        component: RegisterComponent
    },
    {
        path: 'inicio',
        component: Home
    },
    {
        path: 'operaciones-igv',
        loadComponent: () => import('./operacion-igv/operacion-igv').then(m => m.OperacionIGVComponent)
    }


];
