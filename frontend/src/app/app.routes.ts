import { Routes } from '@angular/router';
import { authGuard, loggedInGuard } from './core/auth-guard';

export const routes: Routes = [
    {
        path: '', redirectTo: 'login', pathMatch: 'full'
    },
    {
        path: 'login', canActivate: [loggedInGuard], loadComponent: () => import('./pages/login/login').then((x) => x.Login),
    },
    {
        path: 'register', canActivate: [loggedInGuard], loadComponent: () => import('./pages/register/register').then((x) => x.Register),
    },
    {
        path: 'verify', canActivate: [loggedInGuard], loadComponent: () => import('./pages/verify/verify').then((x) => x.Verify),
    },
    {
        path: 'goals', canActivate: [authGuard], loadComponent: () => import('./pages/goals/goals').then((x) => x.Goals),
    },
    {
        path: 'goal/:id', canActivate: [authGuard], loadComponent: () => import('./pages/goal/goal').then((x) => x.GoalPage),
    },
    {
        path: 'dashboard', canActivate: [authGuard], loadComponent: () => import('./pages/dashboard/dashboard').then((x) => x.Dashboard),
    },
    {
        path: 'source/:id', canActivate: [authGuard], loadComponent: () => import('./pages/funding-source/funding-source').then((x) => x.FundingSourcePage),
    },
    {
        path: 'sources', canActivate: [authGuard], loadComponent: () => import('./pages/funding-source-list/funding-source-list').then((x) => x.FundingSources),
    },
    {
        path: 'contributions', canActivate: [authGuard], loadComponent: () => import('./pages/contributions/contributions').then((x) => x.Contributions),
    },
    {
        path: 'profile', canActivate: [authGuard], loadComponent: () => import('./pages/profile/profile').then((x) => x.Profile),
    },
    {
        path: '**', redirectTo: 'login'
    },
];
