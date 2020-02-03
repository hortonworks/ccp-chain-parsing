import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NzTabsModule } from 'ng-zorro-antd';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { provideMockStore, MockStore } from '@ngrx/store/testing';

import { LiveViewComponent } from './live-view.component';
import { ChainPageState } from '../../chain-page.reducers';
import { LiveViewState } from './live-view.reducers';


@Component({
  selector: 'app-sample-data-form',
  template: ''
})
class MockSampleDataFormComponent {}

describe('LiveViewComponent', () => {
  let component: LiveViewComponent;
  let fixture: ComponentFixture<LiveViewComponent>;

  let store: MockStore<{ 
    'chain-page': ChainPageState, 
    'live-view': LiveViewState 
  }>;
  const initialState = { 
    'chain-page': {
      details: {}
    }, 
    'live-view': {} 
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
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LiveViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
