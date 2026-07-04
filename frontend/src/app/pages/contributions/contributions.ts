import { Component, signal } from '@angular/core';
import { Contribution } from '../../types/Contribution';
import { ContributionCategory, ContributionCategoryLabel } from '../../types/enums/ContributionCategory';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ContributionService } from '../../services/ContributionService';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { ContributionCategoryLabelPipe } from '../../../pipes/contribution-category-label-pipe';

@Component({
  selector: 'app-contributions',
  imports: [TableModule, ContributionCategoryLabelPipe, DialogModule, SelectModule, FormsModule, 
    ReactiveFormsModule, ButtonModule],
  templateUrl: './contributions.html',
  styleUrl: './contributions.css',
})
export class Contributions {

  allContributions = signal<Contribution[]>([]);
  selectedContribution = signal<Contribution | null>(null);
  totalContributions = signal<number>(0);
  showUpdate = signal<boolean>(false);
  showDialog = signal<boolean>(false);
  showDeleteDialog = signal<boolean>(false);
  dialogTitle = signal<string>("");

  contributionCategoryOptions = Object.values(ContributionCategory).map(type => ({
    label: ContributionCategoryLabel[type],
    value: type
  }));
  form! : FormGroup;

  constructor(
    private service: ContributionService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loadContributions();

    this.form = this.formBuilder.group({
      amount: [0, [Validators.required, Validators.min(0)]],
      date: [null, [Validators.required]],
      category: [null, [Validators.required]],
      notes: [""]
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

  saveContribution() {
    if(this.form.invalid) {
      return;
    }

    const {amount, date, category, notes} = this.form.value;

    const categoryKey = Object.entries(ContributionCategory).find(([, val]) => val === category)?.[0];

    const payload: Contribution = {
      amount,
      date,
      category: categoryKey as ContributionCategory,
      notes
    }

    if(this.selectedContribution() === null) {
      this.service.createContribution(payload).subscribe({
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
      notes: ""
    })

    this.showUpdate.set(false);
    this.showDialog.set(true);
  }

  handleUpdateContribution(contribution: Contribution) {
    this.dialogTitle.set("Update Contribution");
    this.selectedContribution.set(contribution);

    this.form.setValue({
      amount: contribution.amount,
      date: contribution.date,
      category: contribution.category,
      notes: contribution.notes
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
