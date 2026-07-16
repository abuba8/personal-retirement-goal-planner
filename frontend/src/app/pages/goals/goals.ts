// src/app/pages/goals/goals.ts
import { Component, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Goal } from '../../types/Goal';
import { GoalService } from '../../services/GoalService';
import { DeleteConfirmationModal } from '../../components/delete-confirmation-modal/delete-confirmation-modal';
import { GoalForm } from '../../components/goal-form/goal-form';
import { TableLazyLoadEvent } from 'primeng/table';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { SideBar } from '../../components/side-bar/side-bar';
import { GoalCard } from '../../components/goal-card/goal-card';
import { ButtonModule } from 'primeng/button';
import { ConfirmationService, MessageService } from 'primeng/api';

@Component({
  selector: 'app-goals',
  imports: [DeleteConfirmationModal, GoalForm, RouterModule, SideBar, 
    ButtonModule, GoalCard, ConfirmDialog],
  templateUrl: './goals.html',
  styleUrl: './goals.css',
})
export class Goals {

  allGoals = signal<Goal[]>([]);                 // goals on the current page
  selectedGoal = signal<Goal | null>(null);      // goal targeted for delete
  showGoalDialog = signal<boolean>(false);
  page = signal<number>(0);
  totalPages = signal<number>(0);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  userName = signal<string>('');

  constructor(
    private goalService: GoalService,
    private router: Router,
    private toastService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.loadGoals();
  }

  loadGoals(event? : TableLazyLoadEvent): void {
    const goalPage = event ? event?.first! / event?.rows! : 0;

    this.loading.set(true);
    this.error.set(null);
    this.goalService.getGoalsPage(goalPage).subscribe({
      next: (data) => {
        this.allGoals.set(data.content);         // the array is inside .content
        this.page.set(data.number);
        this.totalPages.set(data.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set("Could not load goals.");
        console.error(err);
      },
    });
  }

  goToGoal(goal: Goal) {
    this.router.navigate(['/goal', goal.id]);
  }

  handleCreateGoal() {
    this.selectedGoal.set(null);
    this.showGoalDialog.set(true);
  }

  handleUpdateGoal(goal: Goal) {
    this.selectedGoal.set(goal);
    this.showGoalDialog.set(true);
  }

  handleSaveGoal(goal: Goal) {
    if(this.selectedGoal() === null) {
      this.goalService.createGoal(goal).subscribe({
        next: () => {
          this.loadGoals()
        },
        error: (err) => {
          console.error(err)
        }
      });
    } else {
      this.goalService.updateGoal(goal.id!, goal).subscribe({
        next: (data) => {
          this.allGoals.update(
            currentList => currentList.map(g => g.id === data.id ? data : g)
          )
        },
        error: (err) => {
          console.error(err)
        }
      });
    }
  }

  // open the delete modal for a goal (same flow as handleDeleteMovie)
  handleDeleteGoal(goal: Goal): void {
    this.selectedGoal.set(goal);
    this.confirmationService.confirm({
      header: "Confirm Delete",
      message: `Are you sure you want to delete ${goal.name}? This action cannot be undone.`,
      accept: () => this.deleteGoal(goal.id!)
    })
  }

  // modal confirmed -> actually delete
  deleteGoal(goalId: number): void {
    this.goalService.deleteGoal(goalId).subscribe({
      next: () => {
        this.allGoals.update((list) => list.filter((g) => g.id !== goalId));
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
}