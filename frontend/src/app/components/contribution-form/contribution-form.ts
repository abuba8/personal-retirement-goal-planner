import { Component, computed, effect, inject, input, model, output, signal } from '@angular/core';
import { Contribution } from '../../types/Contribution';
import { ContributionCategory, ContributionCategoryLabel } from '../../types/enums/ContributionCategory';
import { FundingSource } from '../../types/FundingSource';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { FundingSourceForm } from '../funding-source-form/funding-source-form';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { InputNumberModule } from 'primeng/inputnumber';
import { GoalService } from '../../services/GoalService';
import { Goal } from '../../types/Goal';
import { GoalForm } from '../goal-form/goal-form';

@Component({
  selector: 'app-contribution-form',
  imports: [ReactiveFormsModule, SelectModule, DialogModule, FundingSourceForm,
    ButtonModule, InputNumberModule, GoalForm
  ],
  templateUrl: './contribution-form.html',
  styleUrl: './contribution-form.css',
})
export class ContributionForm {
  contribution = input<Contribution | null>(null);
  allSources = input<FundingSource[]>([]);
  allGoals = input<Goal[]>([]);
  createdSource = output<FundingSource>();
  createdGoal = output<Goal>();
  lockedSourceId = input<number | null>(null);
  lockedGoalId = input<number | null>(null);
  visible = model<boolean>(false);
  save = output<Contribution>();
  title = computed(() => this.contribution() ? "Update Contribution" : "Log Contribution");
  showCreateSourceDialog = signal(false);
  showCreateGoalDialog = signal(false);

  private formBuilder = inject(FormBuilder);
  form!: FormGroup;

  contributionCategoryOptions = Object.values(ContributionCategory).map(type => ({
    label: ContributionCategoryLabel[type],
    value: type
  }));
  sourceOptions = computed(() => [
    ...this.allSources().map(s => ({ label: s.name, value: s.id })),
  ])
  goalOptions = computed(() => [
    ...this.allGoals().map(g => ({ label: g.name, value: g.id })),
  ])

  constructor(
    private sourceService: FundingSourceService,
    private goalService: GoalService
  ) {
    this.form = this.formBuilder.group({
      amount: [0, [Validators.required, Validators.min(0)]],
      date: [null, [Validators.required]],
      category: [null, [Validators.required]],
      notes: [""],
      sourceId: [1, [Validators.required]],
      goalId: [1, [Validators.required]]
    });

    effect(() => this.resetForm());
  }

  onNewSourceSaved(source: FundingSource) {
    this.sourceService.createSource(source).subscribe({
      next: (data) => {
        this.createdSource.emit(data);
        this.form.get('sourceId')!.setValue(data.id);
      },
      error: (err) => {
        console.error(err)
      }
    });
  }

  onNewGoalSaved(goal: Goal) {
    this.goalService.createGoal(goal).subscribe({
      next: (data) => {
        this.createdGoal.emit(data);
        this.form.get('goalId')!.setValue(data.id);
      },
      error: (err) => {
        console.error(err)
      }
    });
  }

  saveContribution() {
    if(this.form.invalid) {
      return;
    }

    const {amount, date, category, notes, sourceId, goalId} = this.form.value;

    const categoryKey = Object.entries(ContributionCategory).find(([, val]) => val === category)?.[0];

    const payload: Contribution = {
      amount,
      date,
      category: categoryKey as ContributionCategory,
      notes,
      sourceId: this.lockedSourceId() ?? sourceId,
      goalId: this.lockedGoalId() ?? goalId
    }

    if(this.contribution()) {
      payload.id = this.contribution()!.id;
    }

    this.save.emit(payload);
    this.visible.set(false);
  }

  resetForm() {
    const currentContribution = this.contribution();
      this.form.setValue({
      amount: currentContribution?.amount ?? 0,
      date: currentContribution?.date ?? "",
      category: currentContribution?.category ?? null,
      notes: currentContribution?.notes ?? "",
      sourceId: currentContribution?.sourceId ?? this.lockedSourceId() ?? 0,
      goalId: currentContribution?.goalId ?? this.lockedGoalId() ?? 0
    })
  }

  cancel() {
    this.visible.set(false);
    this.resetForm();
  }
}
