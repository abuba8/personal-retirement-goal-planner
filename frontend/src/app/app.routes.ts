import { Routes } from '@angular/router';
import { FundingSources } from "./pages/funding-source-list/funding-source-list"
import { Contributions } from './pages/contributions/contributions';
import { FundingSourcePage } from './pages/funding-source/funding-source';
import { GoalPage } from './pages/goal/goal';

export const routes: Routes = [
    {path: "sources", component: FundingSources},
    {path: "source/:id", component: FundingSourcePage},
    {path: "contributions", component: Contributions},
    {path: "goal/:id", component: GoalPage},
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
];
