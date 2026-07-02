import { Routes } from '@angular/router';
import { FundingSources } from "./pages/funding-sources/funding-sources"
import { Contributions } from './pages/contributions/contributions';

export const routes: Routes = [
    {path: "sources", component: FundingSources},
    {path: "contributions", component: Contributions}
];
