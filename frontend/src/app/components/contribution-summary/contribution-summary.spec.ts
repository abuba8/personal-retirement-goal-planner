import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionSummary } from './contribution-summary';

describe('ContributionSummary', () => {
  let component: ContributionSummary;
  let fixture: ComponentFixture<ContributionSummary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContributionSummary],
    }).compileComponents();

    fixture = TestBed.createComponent(ContributionSummary);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
