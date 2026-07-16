import { Component, signal } from '@angular/core';
import { Contribution } from '../../types/Contribution';
import { ContributionService } from '../../services/ContributionService';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ButtonModule } from 'primeng/button';
import { FundingSource } from '../../types/FundingSource';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { ConfirmationService, MessageService } from 'primeng/api';
import { currencyPipe } from '../../pipes/currency-pipe';
import { ContributionForm } from '../../components/contribution-form/contribution-form';
import { ContributionTable } from '../../components/contribution-table/contribution-table';
import { Goal } from '../../types/Goal';
import { GoalService } from '../../services/GoalService';
import { SideBar } from '../../components/side-bar/side-bar';

@Component({
  selector: 'app-contributions',
  imports: [TableModule, ButtonModule, ContributionForm,
    ConfirmDialog, ContributionTable, SideBar
  ],
  templateUrl: './contributions.html',
  styleUrl: '../utils/css/dashboard/styles.css',
})

export class Contributions {
  allContributions = signal<Contribution[]>([]);
  allSources = signal<FundingSource[]>([]);
  allGoals = signal<Goal[]>([])
  selectedContribution = signal<Contribution | null>(null);
  totalContributions = signal<number>(0);
  showDialog = signal<boolean>(false);
  getSourceName(sourceId?: number): string {
    return this.allSources().find(s => s.id === sourceId)?.name ?? "";
  }
  getGoalName(goalId?: number): string {
    return this.allGoals().find(g => g.id === goalId)?.name ?? "";
  }

  constructor(
    private service: ContributionService,
    private sourceService: FundingSourceService,
    private goalService: GoalService,
    private confirmationService: ConfirmationService,
    private toastService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadContributions();
    this.loadSources();
    this.loadGoals();
  }

  loadContributions(event? : TableLazyLoadEvent) {
    const page = event ? event?.first! / event?.rows! : 0;

    this.service.getContributions(page).subscribe({

      next: (data) => {
        this.allContributions.set(data.content);
        this.totalContributions.set(data.totalElements);
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  loadSources(page: number = 0) {
    this.sourceService.getSources(page).subscribe(data => {
      this.allSources.update(current => page === 0 ? data.content : [...current, ...data.content]);
      if (data.number + 1 < data.totalPages) {
        this.loadSources(data.number + 1);
      }
    });
  }

  loadGoals(page: number = 0) {
    this.goalService.getGoalsPage(page).subscribe(data => {
      this.allGoals.update(current => page === 0 ? data.content : [...current, ...data.content]);
      if (data.number + 1 < data.totalPages) {
        this.loadGoals(data.number + 1);
      }
    });
  }

  handleCreateContribution() {
    this.selectedContribution.set(null);
    this.showDialog.set(true);
  }

  handleSaveContribution(contribution: Contribution) {
    if(this.selectedContribution() === null) {
      this.service.createContribution(contribution, contribution.goalId, contribution.sourceId).subscribe({
        next: () => {
          this.loadContributions()
        },
        error: (err) => {
          console.error(err)
        }
      });
    } else {
      this.service.updateContribution(contribution.id!, contribution, contribution.goalId, contribution.sourceId).subscribe({
        next: (data) => {
          this.allContributions.update(
            currentList => currentList.map(c => c.id === data.id ? data : c)
          )
        },
        error: (err) => {
          console.error(err)
        }
      });
    }
  }

  handleUpdateContribution(contribution: Contribution) {
    this.selectedContribution.set(contribution);
    this.showDialog.set(true);
  }

  handleDeleteContribution(contribution: Contribution) {
    this.selectedContribution.set(contribution);
    this.confirmationService.confirm({
      header: "Confirm Delete",
      message: `Are you sure you want to delete the contribution of ${new currencyPipe().transform(contribution.amount)} to ${this.getGoalName(contribution.goalId)} from
      ${this.getSourceName(contribution.sourceId)}?`,
      accept: () => this.deleteContribution(contribution.id!)
    })
  }

  deleteContribution(contributionId: number) {
    this.service.deleteContribution(contributionId).subscribe({
      next: () => {
        this.loadContributions()
      },
      error: (err) => {
        if(err.status === 409) {
          this.toastService.add({
            severity: "warn",
            summary: "Cannot Delete",
            detail: "This contribution has already occurred and cannot be deleted."
          });
        } else {
          this.toastService.add({
            severity: 'error',
            summary: "Error",
            detail: "Something went wrong. Try again later"
          })
        }
        console.error(err);
      }
    })
  }
}
