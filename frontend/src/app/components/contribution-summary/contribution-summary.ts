import { Component, input } from '@angular/core';
import { currencyPipe } from '../../pipes/currency-pipe';

@Component({
  selector: 'app-contribution-summary',
  imports: [ currencyPipe ],
  templateUrl: './contribution-summary.html',
  styleUrl: './contribution-summary.css',
})
export class ContributionSummary {
  totalContributed = input<number>(0);
  contributionCount = input<number>(0);
}
