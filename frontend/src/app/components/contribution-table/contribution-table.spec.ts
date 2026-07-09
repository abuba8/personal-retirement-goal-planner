import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionTable } from './contribution-table';

describe('ContributionTable', () => {
  let component: ContributionTable;
  let fixture: ComponentFixture<ContributionTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContributionTable],
    }).compileComponents();

    fixture = TestBed.createComponent(ContributionTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
