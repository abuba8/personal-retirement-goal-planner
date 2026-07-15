// src/app/pages/goals/goals.ts
import { Component, signal } from '@angular/core';
<<<<<<< HEAD
=======
import { Router, RouterModule } from '@angular/router';
>>>>>>> origin/main
import { Goal } from '../../types/Goal';
import { GoalService } from '../../services/GoalService';
import { DeleteConfirmationModal } from '../../components/delete-confirmation-modal/delete-confirmation-modal';
import { GoalForm } from '../../components/goal-form/goal-form';
import { TableLazyLoadEvent } from 'primeng/table';
import { SideBar } from '../../components/side-bar/side-bar';

@Component({
  selector: 'app-goals',
<<<<<<< HEAD
  imports: [DeleteConfirmationModal, GoalForm, SideBar],
=======
  imports: [DeleteConfirmationModal, GoalForm, RouterModule, SideBar],
>>>>>>> origin/main
  templateUrl: './goals.html',
  styleUrl: '../utils/css/dashboard/styles.css',
})
export class Goals {

  allGoals = signal<Goal[]>([]);                 // goals on the current page
  selectedGoal = signal<Goal | null>(null);      // goal targeted for delete
  showDeleteDialog = signal<boolean>(false);     // toggle the delete modal
  showGoalDialog = signal<boolean>(false);
  page = signal<number>(0);
  totalPages = signal<number>(0);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  userName = signal<string>('');

  constructor(
    private goalService: GoalService,
    private router: Router
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
    this.showDeleteDialog.set(true);
  }

  // modal confirmed -> actually delete
  deleteGoal(): void {
    const id = this.selectedGoal()!.id!;
    this.goalService.deleteGoal(id).subscribe({
      next: () => {
        this.allGoals.update((list) => list.filter((g) => g.id !== id));
        this.showDeleteDialog.set(false);
      },
      error: (err) => {
        console.error(err);
        this.showDeleteDialog.set(false);
      },
    });
  }
}