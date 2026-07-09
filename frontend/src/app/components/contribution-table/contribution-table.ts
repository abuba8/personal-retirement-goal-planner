import { Component, input, model, output } from '@angular/core';
import { Contribution } from '../../types/Contribution';
import { FundingSource } from '../../types/FundingSource';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { ContributionCategoryLabelPipe } from '../../pipes/contribution-category-label-pipe';
import { DateFormatPipe } from '../../pipes/date-format-pipe';
import { currencyPipe } from '../../pipes/currency-pipe';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-contribution-table',
  imports: [TableModule, ContributionCategoryLabelPipe, DateFormatPipe, 
    currencyPipe, ButtonModule
  ],
  templateUrl: './contribution-table.html',
  styleUrl: './contribution-table.css',
})
export class ContributionTable {
  contributions = input<Contribution[]>([]);
  totalContributions = input<number>(0);
  showSourceColumn = input<boolean>(true);
  allSources = input<FundingSource[]>([]);
  selection = model<Contribution | null>(null);
  lazyLoad = output<TableLazyLoadEvent>();
  select = output<void>();
  create = output<void>();
  getSourceName(sourceId?: number): string {
    return this.allSources().find(s => s.id === sourceId)?.name ?? '';
  }
}
