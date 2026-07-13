import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionLimit } from './contribution-limit';

describe('ContributionLimit', () => {
  let component: ContributionLimit;
  let fixture: ComponentFixture<ContributionLimit>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContributionLimit],
    }).compileComponents();

    fixture = TestBed.createComponent(ContributionLimit);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
