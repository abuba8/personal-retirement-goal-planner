// src/app/pages/goal-form/goal-form.ts
import { Component, computed, effect, inject, input, model, output, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Goal } from '../../types/Goal';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';

@Component({
  selector: 'app-goal-form',
  imports: [ReactiveFormsModule, DialogModule, SelectModule, ButtonModule, InputNumberModule],
  templateUrl: './goal-form.html',
})
export class GoalForm {
  goal = input<Goal | null>(null);
  visible = model<boolean>(false);
  save = output<Goal>();

  title = computed(() => this.goal() ? "Update Goal" : "Create Goal");

  private formBuilder = inject(FormBuilder);
  form!: FormGroup;
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  constructor() {
    this.form = this.formBuilder.group({
      name: ["", [Validators.required, Validators.maxLength(150)]],
      targetRetirementAge: [null, [Validators.required, Validators.min(1)]],
      targetAmount: [null, [Validators.required, Validators.min(0.01)]],
      notes: [""],
    });

    effect(() => this.resetForm());
  }

  saveGoal() {
    if(this.form.invalid) {
      return;
    }

    const {name, targetRetirementAge, targetAmount, notes} = this.form.value;

    const payload: Goal = {
      name,
      targetRetirementAge,
      targetAmount,
      notes
    }

    if(this.goal()) {
      payload.id = this.goal()!.id;
    }

    this.save.emit(payload);
    this.visible.set(false);
  }

  resetForm() {
    const currentGoal = this.goal();
    this.form.setValue({
      name: currentGoal?.name ?? "",
      targetRetirementAge: currentGoal?.targetRetirementAge ?? null,
      targetAmount: currentGoal?.targetAmount ?? null,
      notes: currentGoal?.notes ?? "",
    });
  }

  cancel() {
    this.visible.set(false);
    this.resetForm();
  }
}