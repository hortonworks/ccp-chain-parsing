import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NzTabsModule } from 'ng-zorro-antd';

import { LiveViewComponent } from './live-view.component';


@Component({
  selector: 'app-sample-data-form',
  template: ''
})
class MockSampleDataFormComponent {}


describe('LiveViewComponent', () => {
  let component: LiveViewComponent;
  let fixture: ComponentFixture<LiveViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NzTabsModule,
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
