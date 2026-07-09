import { Component, signal } from '@angular/core';
import { FundingSource } from '../../types/FundingSource';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { SourceTypeLabelPipe } from '../../pipes/source-type-label-pipe';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Router, RouterModule } from '@angular/router';
import { FundingSourceForm } from '../../components/funding-source-form/funding-source-form';
import { ConfirmationService, MessageService } from 'primeng/api';
import { UpdateDialog } from '../../components/update-dialog/update-dialog';


@Component({
  selector: 'app-funding-sources',
  imports: [TableModule, SourceTypeLabelPipe, DialogModule, ConfirmDialog,
    ButtonModule, RouterModule, FundingSourceForm, UpdateDialog],
  templateUrl: './funding-source-list.html',
  styleUrl: './funding-source-list.css',
})
export class FundingSources {

  allSources = signal<FundingSource[]>([]);
  selectedSource = signal<FundingSource | null>(null);
  totalSources = signal<number>(0);
  showUpdate = signal<boolean>(false);
  showDialog = signal<boolean>(false);

  constructor(
    private service: FundingSourceService,
    private router: Router,
    private confirmationService: ConfirmationService,
    private toastService: MessageService
  ){}

  ngOnInit(): void {
    this.loadSources();
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

  goToSource(source: FundingSource) {
    this.router.navigate(['/source', source.id]);
  }

  handleUpdate() {
    this.showUpdate.set(true);
  }

  handleCreateSource() {
    this.selectedSource.set(null);
    this.showUpdate.set(false);
    this.showDialog.set(true);
  }

  handleUpdateSource(source: FundingSource) {
    this.selectedSource.set(source);
    this.showUpdate.set(false);
    this.showDialog.set(true);
  }

  handleSaveSource(source: FundingSource) {
    if(this.selectedSource() === null) {
      this.service.createSource(source).subscribe({
        next: () => {
          this.loadSources()
        },
        error: (err) => {
          console.error(err)
        }
      });
    } else {
      this.service.updateSource(source.id!, source).subscribe({
        next: (data) => {
          this.allSources.update(
            currentList => currentList.map(s => s.id === data.id ? data : s)
          )
        },
        error: (err) => {
          console.error(err)
        }
      });
    }
  }

  handleDeleteSource(source: FundingSource) {
    this.selectedSource.set(source);
    this.showUpdate.set(false);
    this.confirmationService.confirm({
      header: "Confirm Delete",
      message: `Are you sure you want to delete ${source.name}? This action cannot be undone.`,
      accept: () => this.deleteSource(source.id!)
    })
  }

  deleteSource(sourceId: number) {
    this.service.deleteSource(sourceId).subscribe({
      next: () => {
        this.loadSources()
      },
      error: (err) => {
        if(err.status === 409) {
          this.toastService.add({
            severity: "warn",
            summary: "Cannot Delete",
            detail: "This source has contributions, delete the contributions then delete the source."
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
