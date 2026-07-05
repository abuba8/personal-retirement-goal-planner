import { Component, computed, signal } from '@angular/core';
import { Contribution } from '../../types/Contribution';
import { ContributionCategory, ContributionCategoryLabel } from '../../types/enums/ContributionCategory';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ContributionService } from '../../services/ContributionService';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { ContributionCategoryLabelPipe } from '../../../pipes/contribution-category-label-pipe';
import { FundingSource } from '../../types/FundingSource';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { SourceType, SourceTypeLabels } from '../../types/enums/SourceType';

@Component({
  selector: 'app-contributions',
  imports: [TableModule, ContributionCategoryLabelPipe, DialogModule, SelectModule, FormsModule, 
    ReactiveFormsModule, ButtonModule],
  templateUrl: './contributions.html',
  styleUrl: './contributions.css',
})
export class Contributions {

  allContributions = signal<Contribution[]>([]);
  allSources = signal<FundingSource[]>([]);
  // allGoals = signal<Goal[]>([])
  selectedContribution = signal<Contribution | null>(null);
  totalContributions = signal<number>(0);
  showCreateSourceDialog = signal<boolean>(false);
  showUpdate = signal<boolean>(false);
  showDialog = signal<boolean>(false);
  showDeleteDialog = signal<boolean>(false);
  dialogTitle = signal<string>("");
  createNewSource = "CREATE NEW FUNDING SOURCE";

  contributionCategoryOptions = Object.values(ContributionCategory).map(type => ({
    label: ContributionCategoryLabel[type],
    value: type
  }));
  sourceOptions = computed(() => [
    ...this.allSources().map(s => ({ label: s.name, value: s.id })),
    { label: "+ Create New Funding Source", value: this.createNewSource}
  ])
  sourceTypeOptions = Object.values(SourceType).map(type => ({
      label: SourceTypeLabels[type],
      value: type
    }));
  form! : FormGroup;
  sourceForm!: FormGroup;

  constructor(
    private service: ContributionService,
    private sourceService: FundingSourceService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loadContributions();
    this.loadSources();

    this.form = this.formBuilder.group({
      amount: [0, [Validators.required, Validators.min(0)]],
      date: [null, [Validators.required]],
      category: [null, [Validators.required]],
      notes: [""],
      sourceId: [1, [Validators.required]]
    })

    this.sourceForm = this.formBuilder.group({
      name: ["", [Validators.required, Validators.maxLength(150)]],
      institution: ["", [Validators.required, Validators.maxLength(150)]],
      notes: [""],
      type: [null, [Validators.required]]
    })

    this.form.get('sourceId')!.valueChanges.subscribe(value => {
      if(value === this.createNewSource) {
        this.form.get('sourceId')!.setValue(null, { emitEvent: false})
        this.sourceForm.reset()
        this.showCreateSourceDialog.set(true)
      }
    })
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

  loadSources() {
    this.sourceService.getSources(0).subscribe(page => this.allSources.set(page.content));
  }

  saveContribution() {
    if(this.form.invalid) {
      return;
    }

    const {amount, date, category, notes, sourceId} = this.form.value;

    const categoryKey = Object.entries(ContributionCategory).find(([, val]) => val === category)?.[0];

    const payload: Contribution = {
      amount,
      date,
      category: categoryKey as ContributionCategory,
      notes,
    }

    if(this.selectedContribution() === null) {
      this.service.createContribution(payload, 5, sourceId).subscribe({
        next: (data) => {
          this.allContributions.update((currentList) => [...currentList, data])
          this.showDialog.set(false);
        },
        error: (err) => {
          console.error(err);
          this.showDialog.set(false);
        }
      })
    } else {
      payload.id = this.selectedContribution()!.id;
      this.service.updateContribution(payload!.id!, payload).subscribe({
        next: (data) => {
          this.allContributions.update((currentList) => currentList.map(contribution => contribution.id === data.id ? data : contribution))
          this.showDialog.set(false);
        },
        error: (err) => {
          console.error(err);
          this.showDialog.set(false);
        }
      })
    }
  }

  saveNewSource() {
    if(this.sourceForm.invalid) {
      return;
    }

    const {name, institution, notes, type} = this.sourceForm.value;
    const typeKey = Object.entries(SourceType).find(([, val]) => val === type)?.[0];

    const payload: FundingSource = {
      name, 
      institution, 
      notes, 
      type: typeKey as SourceType
    };

    this.sourceService.createSource(payload).subscribe({
      next: (newSource) => {
        this.allSources.update(list => [...list, newSource])
        this.form.get('sourceId')!.setValue(newSource.id)
        this.showCreateSourceDialog.set(false)
      },
      error: (err) => {
        console.error(err)
      }
    })
  }

  handleUpdate() {
    this.showUpdate.set(true);
  }

  handleCreateContribution() {

    this.dialogTitle.set("Create Contribution");
    this.selectedContribution.set(null);

    this.form.setValue({
      amount: 0,
      date: null,
      category: null,
      notes: "",
      sourceId: null
    })

    this.showUpdate.set(false);
    this.showDialog.set(true);
  }

  handleUpdateContribution(contribution: Contribution) {
    this.dialogTitle.set("Update Contribution");
    this.selectedContribution.set(contribution);

    this.form.patchValue({
      amount: contribution.amount,
      date: contribution.date,
      category: contribution.category,
      notes: contribution.notes,
    })

    this.showUpdate.set(false);
    this.showDialog.set(true);
  }

  handleDeleteContribution(contribution: Contribution) {
    console.log(contribution);

    this.selectedContribution.set(contribution);
    this.showUpdate.set(false);
    this.showDeleteDialog.set(true);
  }

  deleteContribution() {
    const contributionId = this.selectedContribution()!.id!;

    this.service.deleteContribution(contributionId).subscribe({
      next: (data) => {
        this.allContributions.update((currentList) => currentList.filter(contribution => contribution.id !== contributionId))
        this.showDeleteDialog.set(false)
      },
      error: (err) => {
        console.error(err);

        this.showDeleteDialog.set(false);
      }
    })
  }
}
