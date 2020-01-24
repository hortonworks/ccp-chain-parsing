import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { NzButtonModule, NzFormModule, NzInputModule } from 'ng-zorro-antd';

import { initialState, LiveViewState } from '../live-view.reducers';

import { SampleDataFormComponent } from './sample-data-form.component';

fdescribe('SampleDataFormComponent', () => {
  let component: SampleDataFormComponent;
  let fixture: ComponentFixture<SampleDataFormComponent>;

  let store: MockStore<LiveViewState>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NzFormModule,
        NzButtonModule,
        NzInputModule,
      ],
      declarations: [ SampleDataFormComponent ],
      providers: [
        provideMockStore({ initialState })
      ]
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
  });
});
