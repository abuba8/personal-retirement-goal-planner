import { Component, input } from '@angular/core';
import { currencyPipe } from '../../pipes/currency-pipe';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-contribution-limit',
  imports: [ currencyPipe, DecimalPipe ],
  templateUrl: './contribution-limit.html',
  styleUrl: './contribution-limit.css',
})
export class ContributionLimit {
  label = input<string>('');
  shape = input<'bar' | 'circle'>('bar');
  showHeader = input<boolean>(true);
  showSummary = input<boolean>(false);
  count = input<number>(0);
  current = input<number>(0);
  target = input<number>(0);
  severity = input<"ok" | "warning" | "danger">("ok");
  message = input<string>('');
  footLeft = input<string>('');
  footRight = input<string>('');

  percentage(): number {
    if(!this.target()) return 0;
    return (this.current() / this.target()) * 100;
  }
}
