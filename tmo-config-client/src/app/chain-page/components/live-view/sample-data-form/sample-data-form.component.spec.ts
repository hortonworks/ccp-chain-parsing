import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { Store } from '@ngrx/store';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { NzButtonModule, NzFormModule, NzInputModule } from 'ng-zorro-antd';

import { sampleDataChanged } from '../live-view.actions';
import { initialState, LiveViewState } from '../live-view.reducers';
import { SampleDataModel } from '../models/sample-data.model';

import { SampleDataFormComponent } from './sample-data-form.component';

describe('SampleDataFormComponent', () => {
  let component: SampleDataFormComponent;
  let fixture: ComponentFixture<SampleDataFormComponent>;

  let store: MockStore<LiveViewState>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
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

    store = TestBed.get(Store);
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
    const sampleDataInput = fixture.debugElement.query(By.css('[data-qe-id="sample-input"]')).nativeElement;
    spyOn(store, 'dispatch');

    sampleDataInput.value = 'test sample data';
    sampleDataInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const sampleData = new SampleDataModel();
    sampleData.source = 'test sample data';

    expect(store.dispatch).toHaveBeenCalledWith(
      sampleDataChanged({ sampleData })
    );
  });
});
