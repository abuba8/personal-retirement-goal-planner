import { Routes } from '@angular/router';
import { FundingSources } from "./pages/funding-source-list/funding-source-list"
import { Contributions } from './pages/contributions/contributions';
import { FundingSourcePage } from './pages/funding-source/funding-source';
import { authGuard } from './core/auth-guard';

export const routes: Routes = [
    {path: "sources", component: FundingSources},
    {path: "source/:id", component: FundingSourcePage},
    {path: "contributions", component: Contributions},
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
        path: 'goals', canActivate: [authGuard], loadComponent: () => import('./pages/goals/goals').then((x) => x.Goals),
    },
    {
        path: 'dashboard', canActivate: [authGuard], loadComponent: () => import('./pages/dashboard/dashboard').then((x) => x.Dashboard),
    },
    {
        path: '**', redirectTo: 'login'
    },
];
