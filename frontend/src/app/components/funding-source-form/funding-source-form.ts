import { Component, computed, effect, inject, input, model, output, signal } from '@angular/core';
import { FundingSource } from '../../types/FundingSource';
import { SourceType, SourceTypeLabels } from '../../types/enums/SourceType';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-funding-source-form',
  imports: [DialogModule, ReactiveFormsModule, SelectModule, ButtonModule],
  templateUrl: './funding-source-form.html',
  styleUrl: './funding-source-form.css',
})
export class FundingSourceForm {
  source = input<FundingSource | null>(null);
  visible = model<boolean>(false);
  save = output<FundingSource>();

  sourceTypeOptions = Object.values(SourceType).map(type => ({
    label: SourceTypeLabels[type],
    value: type
  }));
  title = computed(() => this.source() ? "Update Funding Source" : "Create Funding Source");

  private formBuilder = inject(FormBuilder);
  form!: FormGroup;

    constructor() {
      this.form = this.formBuilder.group({
        name: ["", [Validators.required, Validators.maxLength(150)]],
        institution: ["", [Validators.required, Validators.maxLength(150)]],
        notes: [""],
        type: [null, [Validators.required]]
      });

      effect(() => this.resetForm());
    }

    saveSource() {
    if(this.form.invalid) {
      return;
    }

    const {name, institution, notes, type} = this.form.value;

    const typeKey = Object.entries(SourceType).find(([, val]) => val === type)?.[0];

    const payload: FundingSource = {
      name,
      institution,
      notes,
      type: typeKey as SourceType
    }

    if(this.source()) {
      payload.id = this.source()!.id;
    }

    this.save.emit(payload);
    this.visible.set(false);
  }

  resetForm() {
    const currentSource = this.source();
    this.form.setValue({
      name: currentSource?.name ?? "",
      institution: currentSource?.institution ?? "",
      notes: currentSource?.notes ?? "",
      type: currentSource?.type ?? null
    });
  }

  cancel() {
    this.visible.set(false);
    this.resetForm();
  }
}
