import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { NzButtonModule, NzFormModule, NzInputModule } from 'ng-zorro-antd';

import { initialState, LiveViewState } from '../live-view.reducers';

import { SampleDataFormComponent } from './sample-data-form.component';
import { Store } from '@ngrx/store';
import { sampleDataChanged } from '../live-view.actions';
import { SampleDataType, SampleDataModel } from '../models/sample-data.model';
import { sample } from 'rxjs/operators';

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

  it('apply should be disabled if no sample data', () => {
    const applyBtn = fixture.debugElement.query(By.css('[data-qe-id="apply-button"]')).nativeElement;
    expect(applyBtn.disabled).toBeTruthy();

    fixture.debugElement.query(By.css('[data-qe-id="sample-input"]')).nativeElement.value = 'test sample data';
    fixture.detectChanges();
    expect(applyBtn.disabled).toBeFalsy();

    fixture.debugElement.query(By.css('[data-qe-id="sample-input"]')).nativeElement.value = '';
    fixture.detectChanges();
    expect(applyBtn.disabled).toBeTruthy();
  });

  it('should dispatch change action', () => {
    spyOn(store, 'dispatch');
    fixture.debugElement.query(By.css('[data-qe-id="sample-input"]')).nativeElement.value = 'test sample data';
    fixture.detectChanges();
    fixture.debugElement.query(By.css('[data-qe-id="apply-button"]')).nativeElement.click();

    const sampleData = new SampleDataModel();
    sampleData.source = 'test sample data';

    expect(store.dispatch).toHaveBeenCalledWith(
      sampleDataChanged({ sampleData })
    );
  });
});
