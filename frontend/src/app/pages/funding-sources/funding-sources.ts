import { Component, signal } from '@angular/core';
import { FundingSource } from '../../types/FundingSource';
import { FundingSourceService } from '../../services/FundingSourceSevice';

@Component({
  selector: 'app-funding-sources',
  imports: [],
  templateUrl: './funding-sources.html',
  styleUrl: './funding-sources.css',
})
export class FundingSources {

  allSources = signal<FundingSource[]>([]);
  selectedSource = signal<FundingSource | null>(null);
  totalSources = signal<number>(0);

  constructor(
    private service: FundingSourceService
  ){}

  ngOnInit(): void {
    this.loadSources();
  }

  loadSources(): void {

    const page = 0;

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

  }

  handlgeCreateSource(source: FundingSource) {

  }

  handleDeleteSource(source: FundingSource) {
    
  }


}
