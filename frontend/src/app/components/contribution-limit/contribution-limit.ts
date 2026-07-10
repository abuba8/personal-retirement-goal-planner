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
  yearlyContributed = input<number>(0);
  yearlyLimit = input<number>(0);

  percentage(): number {
    if(!this.yearlyLimit()) return 0;
    return (this.yearlyContributed() / this.yearlyLimit()) * 100;
  }

  severity(): "ok" | "warning" | "danger" {
    if(this.percentage() >= 100) {
      return "danger";
    } else if (this.percentage() >= 75) {
      return "warning";
    }
    return "ok";
  }

  message(): string {
    switch (this.severity()) {
      case "danger": return "Over the 2026 IRS limit for this source type."
      case "warning": return "Approaching the 2026 IRS contribution limit."
      default: return "";
    }
  }
  
}
