import { Component, signal } from '@angular/core';
import { Goal } from '../../types/Goal';
import { FundingSource } from '../../types/FundingSource';
import { Contribution } from '../../types/Contribution';
import { GoalService } from '../../services/GoalService';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { ContributionService } from '../../services/ContributionService';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ContributionTable } from '../../components/contribution-table/contribution-table';
import { ContributionForm } from '../../components/contribution-form/contribution-form';
import { currencyPipe } from '../../pipes/currency-pipe';
import { GoalForm } from '../../components/goal-form/goal-form';
import { SideBar } from '../../components/side-bar/side-bar';
import { GoalCard } from '../../components/goal-card/goal-card';

@Component({
  selector: 'app-goal',
  imports: [RouterModule, TableModule, ButtonModule, ConfirmDialog,
    ContributionTable, ContributionForm, GoalForm, SideBar, GoalCard
  ],
  templateUrl: './goal.html',
  styleUrl: './goal.css',
})
export class GoalPage {
  goalId!: number;
  goal = signal<Goal | null>(null);
  allSources = signal<FundingSource[]>([]);
  contribution = signal<Contribution | null>(null);
  allContributions = signal<Contribution[]>([]);
  totalContributions = signal<number>(0);
  showDialog = signal<boolean>(false);
  showContributionDialog = signal<boolean>(false);
  getSourceName(sourceId?: number): string {
    return this.allSources().find(s => s.id === sourceId)?.name ?? "";
  }

  constructor(
    private goalService: GoalService,
    private sourceService: FundingSourceService,
    private contributionService: ContributionService,
    private router: Router,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private toastService: MessageService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.goalId = Number(params.get("id"));
      this.loadGoal(this.goalId);
      this.loadSources();
      this.loadContributions();
    })
  }

  loadGoal(id: number): void {
    this.goalService.getGoalById(id).subscribe({
      next: (data) => {
        this.goal.set(data);
      },
      error: (err) => {
        console.error(err);
        this.router.navigate(['/goals']);
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

  loadContributions(event?: TableLazyLoadEvent): void {
    const page = event ? event.first! / event.rows! : 0;

    this.contributionService.getContributions(page, this.goalId, undefined).subscribe({
      next: (data) => {
        this.allContributions.set(data.content);
        this.totalContributions.set(data.totalElements);
      },
      error: (err) => console.error(err)
    });
  }

  handleSaveGoal(goal: Goal) {
    this.goalService.updateGoal(this.goalId, goal).subscribe({
      next: (data) => {
        this.goal.set(data);
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  handleDeleteGoal() {
    this.confirmationService.confirm({
      header: "Confirm Delete",
      message: `Are you sure you want to delete ${this.goal()!.name}? This action cannot be undone.`,
      accept: () => this.deleteGoal()
    });
  }

  deleteGoal() {
    this.goalService.deleteGoal(this.goalId).subscribe({
      next: () => {
        this.router.navigate(["/goals"])
      },
      error: (err) => {
        if(err.status === 409) {
          this.toastService.add({
            severity: "warn",
            summary: "Cannot Delete",
            detail: "This goal has contributions, delete the contributions then delete the goal."
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

  handleCreateContribution() {
      this.contribution.set(null);
      this.showContributionDialog.set(true);
    }
  
  handleSaveContribution(contribution: Contribution) {
    if(this.contribution() === null) {
      this.contributionService.createContribution(contribution, contribution.goalId, contribution.sourceId).subscribe({
        next: () => {
          this.loadContributions();
          this.loadGoal(this.goalId);
        },
        error: (err) => {
          console.error(err)
        }
      });
    } else {
      this.contributionService.updateContribution(contribution.id!, contribution, contribution.goalId, contribution.sourceId).subscribe({
        next: () => {
          this.loadContributions();
          this.loadGoal(this.goalId);
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
      message: `Are you sure you want to delete the contribution of ${new currencyPipe().transform(contribution.amount)} to ${this.goal()!.name} from
      ${this.getSourceName(contribution.sourceId)}?`,
      accept: () => this.deleteContribution(contribution.id!)
    })
  }

  deleteContribution(contributionId: number) {
    this.contributionService.deleteContribution(contributionId).subscribe({
      next: () => {
        this.loadContributions();
        this.loadGoal(this.goalId);
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
