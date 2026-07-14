import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FundingSourcePage } from './funding-source';
import { RouterLink } from '@angular/router';

describe('FundingSourcePage', () => {
  let component: FundingSourcePage;
  let fixture: ComponentFixture<FundingSourcePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FundingSourcePage, RouterLink],
    }).compileComponents();

    fixture = TestBed.createComponent(FundingSourcePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
