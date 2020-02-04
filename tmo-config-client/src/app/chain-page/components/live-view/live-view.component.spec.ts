import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { NzTabsModule } from 'ng-zorro-antd';
import { NzSpinModule } from 'ng-zorro-antd/spin';

import { LiveViewComponent } from './live-view.component';
import { SampleDataModel, SampleDataType } from './models/sample-data.model';
import { Store } from '@ngrx/store';
import { executionTriggered } from './live-view.actions';

@Component({
  selector: 'app-sample-data-form',
  template: ''
})
class MockSampleDataFormComponent {
  @Input() sampleData: SampleDataModel;
}

describe('LiveViewComponent', () => {
  let component: LiveViewComponent;
  let fixture: ComponentFixture<LiveViewComponent>;

  let mockStore: MockStore<{
    'chain-page': {
      details: {}
    },
    'live-view': {}
  }>

  const initialState = {
    'chain-page': {
      details: {}
    },
    'live-view': {
      sampleData: {
        type: SampleDataType.MANUAL,
        source: '',
      }
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
      ],
      providers: [
        provideMockStore({ initialState }),
      ],
      declarations: [ LiveViewComponent, MockSampleDataFormComponent ]
    })
    .compileComponents();

    mockStore = TestBed.get(Store);
    spyOn(mockStore, 'dispatch').and.callThrough();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LiveViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should react on sampleData change', fakeAsync(() => {
    component.sampleDataChange$.next(testSampleData);

    tick(component.LIVE_VIEW_DEBOUNCE_RATE);

    expect(mockStore.dispatch).toHaveBeenCalledWith({
      sampleData: testSampleData,
      chainConfig: {},
      type: executionTriggered.type
    });
  }));

  it('should react on chain config change', fakeAsync(() => {
    component.sampleDataChange$.next(testSampleData);
    mockStore.setState({
      ...initialState,
      "chain-page": {
        details: {
          id: '123',
          name: 'abcdefg',
          parsers: [],
        }
      }
    });

    tick(component.LIVE_VIEW_DEBOUNCE_RATE);

    expect(mockStore.dispatch).toHaveBeenCalledWith({
      sampleData: testSampleData,
      chainConfig: { id: '123', name: 'abcdefg', parsers: [] },
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
    mockStore.setState({
      ...initialState,
      "chain-page": {
        details: {
          id: '123',
          name: 'abcdefg',
          parsers: [],
        }
      }
    });

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
