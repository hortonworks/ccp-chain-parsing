import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { NzTabsModule } from 'ng-zorro-antd';
import { NzSpinModule } from 'ng-zorro-antd/spin';

import { LiveViewComponent } from './live-view.component';
import { SampleDataModel } from './models/sample-data.model';

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
