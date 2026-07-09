import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionForm } from './contribution-form';

describe('ContributionForm', () => {
  let component: ContributionForm;
  let fixture: ComponentFixture<ContributionForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContributionForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ContributionForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
