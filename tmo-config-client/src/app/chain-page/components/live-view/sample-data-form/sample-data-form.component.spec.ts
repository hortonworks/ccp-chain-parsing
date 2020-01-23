import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SampleDataFormComponent } from './sample-data-form.component';
import { By } from '@angular/platform-browser';

describe('SampleDataFormComponent', () => {
  let component: SampleDataFormComponent;
  let fixture: ComponentFixture<SampleDataFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SampleDataFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SampleDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch change action', () => {
    fixture.debugElement.query(By.css('[data-qe-id="apply-button"]')).nativeElement.click();
  })
});
