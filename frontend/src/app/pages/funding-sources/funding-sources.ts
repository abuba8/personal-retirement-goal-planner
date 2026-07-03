import { Component, signal } from '@angular/core';
import { FundingSource } from '../../types/FundingSource';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { SourceTypeLabelPipe } from '../../../pipes/source-type-label-pipe';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SourceType } from '../../types/enums/SourceType';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';


@Component({
  selector: 'app-funding-sources',
  imports: [TableModule, SourceTypeLabelPipe, DialogModule, SelectModule, 
    FormsModule, ReactiveFormsModule, ButtonModule],
  templateUrl: './funding-sources.html',
  styleUrl: './funding-sources.css',
})
export class FundingSources {

  allSources = signal<FundingSource[]>([]);
  selectedSource = signal<FundingSource | null>(null);
  totalSources = signal<number>(0);
  showUpdate = signal<boolean>(false);
  showDialog = signal<boolean>(false);
  showDeleteDialog = signal<boolean>(false);
  dialogTitle = signal<string>("");

  sourceTypeOptions = Object.values(SourceType);
  form! : FormGroup;

  constructor(
    private service: FundingSourceService,
    private formBuilder: FormBuilder
  ){}

  ngOnInit(): void {
    this.loadSources();

    this.form = this.formBuilder.group({
      name: ["", [Validators.required, Validators.max(150)]],
      institution: ["", [Validators.required, Validators.max(150)]],
      notes: [""],
      type: [null, [Validators.required]]
    })
  }

  loadSources(event? : TableLazyLoadEvent) {
    const page = event ? event?.first! / event?.rows! : 0;

    this.service.getSources(page).subscribe({

      next: (data) => {
        this.allSources.set(data.content);
        this.totalSources.set(data.totalElements);   
      },
      error: (err) => {
        console.error(err);
      }
    });
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

    if(this.selectedSource() === null) {
      this.service.createSource(payload).subscribe({
        next: (data) => {
          this.allSources.update((currentList) => [...currentList, data])
          this.showDialog.set(false);
        },
        error: (err) => {
          console.error(err);
          this.showDialog.set(false);
        }
      })
    } else {
      payload.id = this.selectedSource()!.id;
      this.service.updateSource(payload!.id!, payload).subscribe({
        next: (data) => {
          this.allSources.update((currentList) => currentList.map(source => source.id === data.id ? data : source))
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

  handleCreateSource() {

    this.dialogTitle.set("Create Funding Souce");

    this.form.setValue({
      name: "",
      institution: "",
      notes: "",
      type: null
    })

    this.showUpdate.set(false);
    this.showDialog.set(true);
  }

  handleUpdateSource(source: FundingSource) {
    this.dialogTitle.set("Update Funding Source");
    this.selectedSource.set(source);

    this.form.setValue({
      name: source.name,
      institution: source.institution,
      notes: source.notes,
      type: source.type
    })

    this.showUpdate.set(false);
    this.showDialog.set(true);
  }

  handleDeleteSource(source: FundingSource) {
    console.log(source);

    this.selectedSource.set(source);
    this.showUpdate.set(false);
    this.showDeleteDialog.set(true);
  }

  deleteSource() {
    const sourceId = this.selectedSource()!.id!;

    this.service.deleteSource(sourceId).subscribe({
      next: (data) => {
        this.allSources.update((currentList) => currentList.filter(source => source.id !== sourceId))
        this.showDeleteDialog.set(false);
      },
      error: (err) => {
        console.error(err);

        this.showDeleteDialog.set(false);
      }
    })
  }
}
