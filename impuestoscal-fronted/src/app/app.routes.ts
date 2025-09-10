import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Home } from './dashboard/home/home';

export const routes: Routes = [
    {
        path: 'iniciar-sesion',
        component: Login
    },
    {
        path: 'registrarse',
        component: Register
    },
    {
        path: 'inicio',
        component: Home
    },

];
