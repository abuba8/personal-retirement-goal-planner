import { Component, effect, inject, input, output, signal } from '@angular/core';
import { FundingSource } from '../../types/FundingSource';
import { Contribution } from '../../types/Contribution';
import { SourceTypeLabelPipe } from '../../pipes/source-type-label-pipe';
import { ContributionLimit } from '../contribution-limit/contribution-limit';
import { currencyPipe } from '../../pipes/currency-pipe';
import { ButtonModule } from 'primeng/button';
import { ContributionService } from '../../services/ContributionService';
import { SourceTypeLimit } from '../../types/enums/SourceType';

@Component({
  selector: 'app-source-card',
  imports: [SourceTypeLabelPipe, ContributionLimit, currencyPipe, ButtonModule],
  templateUrl: './source-card.html',
  styleUrl: './source-card.css',
})
export class SourceCard {
  source = input.required<FundingSource>();
  variant = input<'grid' | 'vertical'>('grid');
  open = output<FundingSource>();
  edit = output<FundingSource>();
  delete = output<FundingSource>();

  totalContributed = signal<number>(0);
  contributionCount = signal<number>(0);
  yearlyContributed = signal<number>(0);
  yearlyLimit = signal<number>(0);

  private contributionService = inject(ContributionService);
  
  constructor() {
    effect(() => {
      this.yearlyLimit.set(SourceTypeLimit[this.source().type] ?? 0);
      this.loadContributionSummary(this.source().id!);
    });
  }

  loadContributionSummary(sourceId: number) {
    this.contributionService.getContributions(0, undefined, sourceId).subscribe({
      next: (firstPage) => {
        this.contributionCount.set(firstPage.totalElements);
        this.accumulateAmounts(sourceId, firstPage.content, 1, firstPage.totalPages);
      },
      error: (err) => {
        console.error(err)
      }
    });
  }

  accumulateAmounts(sourceId: number, soFar: Contribution[], nextPage: number, totalPages: number) {
    if (nextPage >= totalPages) {
      this.totalContributed.set(soFar.reduce((sum, c) => sum + c.amount, 0));
      const thisYear = soFar.filter(c => new Date(c.date).getFullYear() === new Date().getFullYear());
      this.yearlyContributed.set(thisYear.reduce((sum, c) => sum + c.amount, 0));
      return;
    }
    this.contributionService.getContributions(nextPage, undefined, sourceId).subscribe({
      next: (page) => {
        this.accumulateAmounts(sourceId, [...soFar, ...page.content], nextPage + 1, totalPages)
      },
      error: (err) => {
        console.error(err)
      }
    })
  }

  limitPercentage(): number {
    if (!this.yearlyLimit()) return 0;
    return (this.yearlyContributed() / this.yearlyLimit()) * 100;
  }

  limitSeverity(): "ok" | "warning" | "danger" {
    if (this.limitPercentage() >= 100) return "danger";
    if (this.limitPercentage() >= 75) return "warning";
    return "ok";
  }

  limitMessage(): string {
    switch (this.limitSeverity()) {
      case "danger": return "Over the 2026 IRS limit for this source type.";
      case "warning": return "Approaching the 2026 IRS contribution limit.";
      default: return "";
    }
  }
}
