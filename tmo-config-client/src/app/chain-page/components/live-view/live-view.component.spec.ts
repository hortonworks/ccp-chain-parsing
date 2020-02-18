import { Component, Input } from '@angular/core';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Store } from '@ngrx/store';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { NzTabsModule } from 'ng-zorro-antd';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { Subject } from 'rxjs';

import { executionTriggered } from './live-view.actions';
import { LiveViewComponent } from './live-view.component';
import { LiveViewState } from './live-view.reducers';
import { LiveViewResultModel } from './models/live-view.model';
import { SampleDataModel, SampleDataType } from './models/sample-data.model';

@Component({
  selector: 'app-sample-data-form',
  template: ''
})
class MockSampleDataFormComponent {
  @Input() sampleData: SampleDataModel;
}

@Component({
  selector: 'app-live-view-result',
  template: ''
})
class MockLiveViewResultComponent {
  @Input() results: LiveViewResultModel;
}

describe('LiveViewComponent', () => {
  let component: LiveViewComponent;
  let fixture: ComponentFixture<LiveViewComponent>;

  let mockStore: MockStore<LiveViewState>;

  const initialState = { 'live-view': {
      sampleData: {
        type: SampleDataType.MANUAL,
        source: '',
      },
      isExecuting: false,
      result: undefined,
    }
  };

  const testSampleData = {
    type: SampleDataType.MANUAL,
    source: 'test sample data input',
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NzTabsModule,
        NzSpinModule,
        RouterTestingModule
      ],
      providers: [
        provideMockStore({ initialState }),
      ],
      declarations: [
        LiveViewComponent,
        MockSampleDataFormComponent,
        MockLiveViewResultComponent,
      ]
    })
    .compileComponents();

    mockStore = TestBed.get(Store);
    spyOn(mockStore, 'dispatch').and.callThrough();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LiveViewComponent);
    component = fixture.componentInstance;
    component.chainConfig$ = new Subject<{}>();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should react on sampleData change', fakeAsync(() => {
    component.sampleDataChange$.next(testSampleData);
    (component.chainConfig$ as Subject<{}>).next({});

    tick(component.LIVE_VIEW_DEBOUNCE_RATE);

    expect(mockStore.dispatch).toHaveBeenCalledWith({
      sampleData: testSampleData,
      chainConfig: {},
      type: executionTriggered.type
    });
  }));

  it('should react on chain config change', fakeAsync(() => {
    component.sampleDataChange$.next(testSampleData);
    (component.chainConfig$ as Subject<{}>).next({});

    tick(component.LIVE_VIEW_DEBOUNCE_RATE);

    expect(mockStore.dispatch).toHaveBeenCalledWith({
      sampleData: testSampleData,
      chainConfig: {},
      type: executionTriggered.type
    });
  }));

  it('should filter out events without sample data input', fakeAsync(() => {
    component.sampleDataChange$.next({
      type: SampleDataType.MANUAL,
      source: '',
    });

    tick(component.LIVE_VIEW_DEBOUNCE_RATE);

    expect(mockStore.dispatch).not.toHaveBeenCalled();
  }));

  it('should hold back (debounce) executeTriggered', fakeAsync(() => {
    component.sampleDataChange$.next(testSampleData);
    (component.chainConfig$ as Subject<{}>).next({});

    tick(component.LIVE_VIEW_DEBOUNCE_RATE / 2);

    expect(mockStore.dispatch).not.toHaveBeenCalled();

    tick(component.LIVE_VIEW_DEBOUNCE_RATE / 2);

    expect(mockStore.dispatch).toHaveBeenCalled();
  }));

  it('should unsubscribe on destroy', fakeAsync(() => {
    component.ngOnDestroy();
    component.sampleDataChange$.next(testSampleData);

    tick(component.LIVE_VIEW_DEBOUNCE_RATE);

    expect(mockStore.dispatch).not.toHaveBeenCalled();
  }));
});
