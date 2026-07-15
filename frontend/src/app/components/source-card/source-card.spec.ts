import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SourceCard } from './source-card';

describe('SourceCard', () => {
  let component: SourceCard;
  let fixture: ComponentFixture<SourceCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SourceCard],
    }).compileComponents();

    fixture = TestBed.createComponent(SourceCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
