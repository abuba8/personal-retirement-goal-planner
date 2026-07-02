import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FundingSources } from './funding-sources';

describe('FundingSources', () => {
  let component: FundingSources;
  let fixture: ComponentFixture<FundingSources>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FundingSources],
    }).compileComponents();

    fixture = TestBed.createComponent(FundingSources);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
