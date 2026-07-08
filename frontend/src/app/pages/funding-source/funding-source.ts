import { Component, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { ContributionService } from '../../services/ContributionService';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { SourceTypeLabelPipe } from '../../../pipes/source-type-label-pipe';
import { FundingSource } from '../../types/FundingSource';
import { Contribution } from '../../types/Contribution';
import { ContributionCategoryLabelPipe } from '../../../pipes/contribution-category-label-pipe';
import { FundingSourceForm } from '../../components/funding-source-form/funding-source-form';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-funding-source',
  imports: [RouterModule, TableModule, ButtonModule, SourceTypeLabelPipe, 
    ContributionCategoryLabelPipe, FundingSourceForm, ConfirmDialog
  ],
  templateUrl: './funding-source.html',
  styleUrl: './funding-source.css',
})
export class FundingSourcePage {
  sourceId!: number;
  source = signal<FundingSource | null>(null);
  allContributions = signal<Contribution[]>([]);
  totalContributions = signal<number>(0);
  showDialog = signal<boolean>(false);

  constructor(
    private sourceService: FundingSourceService,
    private contributionService: ContributionService,
    private router: Router,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.sourceId = Number(params.get('id'));
      this.loadSource(this.sourceId);
      this.loadContributions();
    })
  }

  loadSource(id: number): void {
    this.sourceService.getSourceById(id).subscribe({
      next: (data) => this.source.set(data),
      error: (err) => {
        console.error(err);
        this.router.navigate(['/sources']);
      }
    });
  }

  loadContributions(event?: TableLazyLoadEvent): void {
    const page = event ? event.first! / event.rows! : 0;

    this.contributionService.getContributions(page, undefined, this.sourceId).subscribe({
      next: (data) => {
        this.allContributions.set(data.content);
        this.totalContributions.set(data.totalElements);
      },
      error: (err) => console.error(err)
    });
  }

  handleSaveSource(source: FundingSource) {
    this.sourceService.updateSource(this.sourceId, source).subscribe({
      next: (data) => {
        this.source.set(data)
      },
      error: (err) => {
        console.error(err)
      }
    });
  }

  handleDeleteSource() {
    this.confirmationService.confirm({
      header: "Confirm Delete",
      message: `Are you sure you want to delete ${this.source()!.name}? This action cannot be undone.`,
      accept: () => this.deleteSource()
    });
  }

  deleteSource() {
    this.sourceService.deleteSource(this.sourceId).subscribe({
      next: () => {
        this.router.navigate(['/sources'])
      },
      error: (err) => {
        console.error(err)
      }
    });
  }
}
