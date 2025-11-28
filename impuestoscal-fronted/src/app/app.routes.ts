import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { Register } from './auth/register/register';
import { Home } from './dashboard/home/home';
import { DashboardLayout } from './layout/dashboard-layout';

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
        path: '',
        component: DashboardLayout,
        children: [
            { path: 'inicio', component: Home },
            { path: '', redirectTo: 'inicio', pathMatch: 'full' }
        ]
    }
];
