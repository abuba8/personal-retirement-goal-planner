import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { currencyPipe } from '../../pipes/currency-pipe';
import { ButtonModule } from 'primeng/button';
import { Goal } from '../../types/Goal';
import { ContributionService } from '../../services/ContributionService';
import { Contribution } from '../../types/Contribution';
import { ContributionLimit } from '../contribution-limit/contribution-limit';

@Component({
  selector: 'app-goal-card',
  imports: [currencyPipe, ButtonModule, ContributionLimit],
  templateUrl: './goal-card.html',
  styleUrl: './goal-card.css',
})
export class GoalCard {
  goal = input.required<Goal>();
  variant = input<'grid' | 'vertical'>('grid');
  open = output<Goal>();
  edit = output<Goal>();
  delete = output<Goal>();

  totalContributed = signal<number>(0);
  contributionCount = signal<number>(0);

  percentFunded = computed(() => {
    const target = this.goal().targetAmount;
    if (!target) return 0;
    return Math.round((this.totalContributed() / target) * 100);
  });

  goalStatus(): string {
    const percent = this.percentFunded();
    if (percent >= 100) return 'Achieved';
    if (percent >= 40) return 'On Track';
    return 'At Risk';
  }

  progressSeverity(): "ok" | "warning" | "danger" {
    return this.percentFunded() >= 40 ? "ok" : "danger";
  }

  remainingAmount(): number {
    return Math.max((this.goal().targetAmount ?? 0) - this.totalContributed(), 0);
  }

  private contributionService = inject(ContributionService);

  constructor() {
    effect(() => {
      this.loadContributionSummary(this.goal().id!);
    });
  }

  loadContributionSummary(goalId: number) {
    this.contributionService.getContributions(0, goalId).subscribe({
      next: (firstPage) => {
        this.contributionCount.set(firstPage.totalElements);
        this.accumulateAmounts(goalId, firstPage.content, 1, firstPage.totalPages);
      },
      error: (err) => {
        console.error(err)
      }
    });
  }

  accumulateAmounts(goalId: number, soFar: Contribution[], nextPage: number, totalPages: number) {
    if (nextPage >= totalPages) {
      this.totalContributed.set(soFar.reduce((sum, c) => sum + c.amount, 0));
      return;
    }
    this.contributionService.getContributions(nextPage, goalId).subscribe({
      next: (page) => {
        this.accumulateAmounts(goalId, [...soFar, ...page.content], nextPage + 1, totalPages)
      },
      error: (err) => {
        console.error(err)
      }
    })
  }
}
