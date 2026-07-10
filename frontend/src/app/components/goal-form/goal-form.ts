// src/app/pages/goal-form/goal-form.ts
import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Goal } from '../../types/Goal';
import { GoalService } from '../../services/GoalService';

@Component({
  selector: 'app-goal-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './goal-form.html',
})
export class GoalForm {

  form!: FormGroup;
  editingId = signal<number | null>(null);   // null = create, number = edit
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  constructor(
    private formBuilder: FormBuilder,
    private goalService: GoalService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      name: ["", [Validators.required, Validators.maxLength(150)]],
      targetRetirementAge: [null, [Validators.required, Validators.min(1)]],
      targetAmount: [null, [Validators.required, Validators.min(0.01)]],
      notes: [""],
    });

    // if the URL has an :id, we're editing -> load the goal and prefill
    const idParam = this.route.snapshot.paramMap.get("id");
    if (idParam) {
      const id = Number(idParam);
      this.editingId.set(id);
      this.goalService.getGoalById(id).subscribe({
        next: (goal) => this.form.patchValue(goal),   // fill matching controls
        error: () => this.error.set("Could not load the goal."),
      });
    }
  }

  submit(): void {
    if (this.form.invalid) { return; }
    this.loading.set(true);
    this.error.set(null);

    const payload: Goal = this.form.value;
    const id = this.editingId();

    // same create-vs-update branch as saveMovie()
    const request = id
      ? this.goalService.updateGoal(id, payload)
      : this.goalService.createGoal(payload);

    request.subscribe({
      next: () => this.router.navigate(["/goals"]),
      error: (err) => {
        this.loading.set(false);
        this.error.set(typeof err.error === "string" ? err.error : "Save failed.");
      },
    });
  }
}