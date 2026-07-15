import { Component, input, model, output } from '@angular/core';
import { Contribution } from '../../types/Contribution';
import { FundingSource } from '../../types/FundingSource';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { ContributionCategoryLabelPipe } from '../../pipes/contribution-category-label-pipe';
import { DateFormatPipe } from '../../pipes/date-format-pipe';
import { currencyPipe } from '../../pipes/currency-pipe';
import { ButtonModule } from 'primeng/button';
import { Goal } from '../../types/Goal';

@Component({
  selector: 'app-contribution-table',
  imports: [TableModule, ContributionCategoryLabelPipe, DateFormatPipe, 
    currencyPipe, ButtonModule
  ],
  templateUrl: './contribution-table.html',
  styleUrl: '../../pages/utils/css/dashboard/styles.css', 
})
export class ContributionTable {
  contributions = input<Contribution[]>([]);
  totalContributions = input<number>(0);
  variant = input<'table' | 'compact'>('table');
  showSourceColumn = input<boolean>(true);
  showGoalColumn = input<boolean>(true);
  allSources = input<FundingSource[]>([]);
  allGoals = input<Goal[]>([]);
  selection = model<Contribution | null>(null);
  lazyLoad = output<TableLazyLoadEvent>();
  select = output<void>();
  create = output<void>();
  getSourceName(sourceId?: number): string {
    return this.allSources().find(s => s.id === sourceId)?.name ?? "";
  }
  getGoalName(goalId?: number): string {
    return this.allGoals().find(g => g.id === goalId)?.name ?? "";
  }
}
