import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '', redirectTo: 'login', pathMatch: 'full'
    },
    {
        path: 'login', loadComponent: () => import('./pages/login/login').then((x) => x.Login),
    },
    {
        path: 'register', loadComponent: () => import('./pages/register/register').then((x) => x.Register),
    },
    {
        path: 'verify', loadComponent: () => import('./pages/verify/verify').then((x) => x.Verify),
    },
    {
        path: 'goals', loadComponent: () => import('./pages/goals/goals').then((x) => x.Goals),
    },
    {
        path: '**', redirectTo: 'login'
    },
]