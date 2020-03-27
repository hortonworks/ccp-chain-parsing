import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NzCardModule } from 'ng-zorro-antd';

import { LiveViewResultComponent } from './live-view-result.component';

@Component({
  selector: 'app-parser-by-parser',
  template: '',
})
export class MockParserByParserComponent {
  @Input() parserResults = [];
}

describe('LiveViewResultComponent', () => {
  let component: LiveViewResultComponent;
  let fixture: ComponentFixture<LiveViewResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LiveViewResultComponent, MockParserByParserComponent ],
      imports: [ NzCardModule ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LiveViewResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
