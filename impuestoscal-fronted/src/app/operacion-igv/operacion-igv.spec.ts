import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OperacionIgv } from './operacion-igv';

describe('OperacionIgv', () => {
  let component: OperacionIgv;
  let fixture: ComponentFixture<OperacionIgv>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperacionIgv]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OperacionIgv);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
