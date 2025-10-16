import { Routes } from '@angular/router';
import { LoginComponent  } from './auth/login/login';
import { Register } from './auth/register/register';
import { Home } from './dashboard/home/home';

export const routes: Routes = [
    {
        path: 'iniciar-sesion',
        component: LoginComponent 
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
