import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FundingSourceForm } from './funding-source-form';

describe('FundingSourceForm', () => {
  let component: FundingSourceForm;
  let fixture: ComponentFixture<FundingSourceForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FundingSourceForm],
    }).compileComponents();

    fixture = TestBed.createComponent(FundingSourceForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
