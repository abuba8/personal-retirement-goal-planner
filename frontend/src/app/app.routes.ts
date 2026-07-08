import { Routes } from '@angular/router';
import { FundingSources } from "./pages/funding-source-list/funding-source-list"
import { Contributions } from './pages/contributions/contributions';
import { FundingSourcePage } from './pages/funding-source/funding-source';

export const routes: Routes = [
    {path: "sources", component: FundingSources},
    {path: "source/:id", component: FundingSourcePage},
    {path: "contributions", component: Contributions}
];
