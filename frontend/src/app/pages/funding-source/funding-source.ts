import { Component, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { ContributionService } from '../../services/ContributionService';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { FundingSource } from '../../types/FundingSource';
import { Contribution } from '../../types/Contribution';
import { FundingSourceForm } from '../../components/funding-source-form/funding-source-form';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ContributionTable } from '../../components/contribution-table/contribution-table';
import { currencyPipe } from '../../pipes/currency-pipe';
import { ContributionForm } from '../../components/contribution-form/contribution-form';
import { Goal } from '../../types/Goal';
import { GoalService } from '../../services/GoalService';
import { SourceCard } from '../../components/source-card/source-card';
import { SideBar } from '../../components/side-bar/side-bar';

@Component({
  selector: 'app-funding-source',
  imports: [RouterModule, TableModule, ButtonModule, FundingSourceForm, SideBar,
    ConfirmDialog, ContributionTable, ContributionForm, SourceCard
  ],
  templateUrl: './funding-source.html',
  styleUrl: './funding-source.css',
})
export class FundingSourcePage {
  sourceId!: number;
  source = signal<FundingSource | null>(null);
  allGoals = signal<Goal[]>([]);
  contribution = signal<Contribution | null>(null);
  allContributions = signal<Contribution[]>([]);
  totalContributions = signal<number>(0);
  showDialog = signal<boolean>(false);
  showContributionDialog = signal<boolean>(false);
  getGoalName(goalId?: number): string {
    return this.allGoals().find(g => g.id === goalId)?.name ?? "";
  }

  constructor(
    private sourceService: FundingSourceService,
    private goalService: GoalService,
    private contributionService: ContributionService,
    private router: Router,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private toastService: MessageService,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.sourceId = Number(params.get('id'));
      this.loadSource(this.sourceId);
      this.loadGoals();
      this.loadContributions();
    })
  }

  loadSource(id: number): void {
    this.sourceService.getSourceById(id).subscribe({
      next: (data) => {
        this.source.set(data);
      },
      error: (err) => {
        console.error(err);
        this.router.navigate(['/sources']);
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

  loadContributions(event?: TableLazyLoadEvent): void {
    const page = event ? event.first! / event.rows! : 0;

    this.contributionService.getContributions(page, undefined, this.sourceId).subscribe({
      next: (data) => {
        this.allContributions.set(data.content);
        this.totalContributions.set(data.totalElements);
      },
      error: (err) => console.error(err)
    });
  }

  handleSaveSource(source: FundingSource) {
    this.sourceService.updateSource(this.sourceId, source).subscribe({
      next: (data) => {
        this.source.set(data);
      },
      error: (err) => {
        console.error(err)
      }
    });
  }

  handleDeleteSource() {
    this.confirmationService.confirm({
      header: "Confirm Delete",
      message: `Are you sure you want to delete ${this.source()!.name}? This action cannot be undone.`,
      accept: () => this.deleteSource()
    });
  }

  deleteSource() {
    this.sourceService.deleteSource(this.sourceId).subscribe({
      next: () => {
        this.router.navigate(['/sources'])
      },
      error: (err) => {
        if(err.status === 409) {
          this.toastService.add({
            severity: "warn",
            summary: "Cannot Delete",
            detail: "This source has contributions, delete the contributions then delete the source."
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
    });
  }

  handleCreateContribution() {
    this.contribution.set(null);
    this.showContributionDialog.set(true);
  }

  handleSaveContribution(contribution: Contribution) {
    if(this.contribution() === null) {
      this.contributionService.createContribution(contribution, contribution.goalId, contribution.sourceId).subscribe({
        next: () => {
          this.loadContributions();
          this.loadSource(this.sourceId);
        },
        error: (err) => {
          console.error(err)
        }
      });
    } else {
      this.contributionService.updateContribution(contribution.id!, contribution, contribution.goalId, contribution.sourceId).subscribe({
        next: () => {
          this.loadContributions();
          this.loadSource(this.sourceId);
        },
        error: (err) => {
          console.error(err)
        }
      });
    }
  }

  handleUpdateContribution(contribution: Contribution) {
    this.contribution.set(contribution);
    this.showContributionDialog.set(true);
  }

  handleDeleteContribution(contribution: Contribution) {
    this.contribution.set(contribution);
    this.confirmationService.confirm({
      header: "Confirm Delete",
      message: `Are you sure you want to delete the contribution of ${new currencyPipe().transform(contribution.amount)} to ${this.getGoalName(contribution.goalId)} from
      ${this.source()?.name}?`,
      accept: () => this.deleteContribution(contribution.id!)
    })
  }

  deleteContribution(contributionId: number) {
    this.contributionService.deleteContribution(contributionId).subscribe({
      next: () => {
        this.loadContributions();
        this.loadSource(this.sourceId);
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
