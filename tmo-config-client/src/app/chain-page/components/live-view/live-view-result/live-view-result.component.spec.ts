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
})

@Component({
  selector: 'app-stack-trace',
  template: '',
})
class FakeStackTraceComponent {
  @Input() stackTraceMsg = '';
}

describe('LiveViewResultComponent', () => {
  let component: LiveViewResultComponent;
  let fixture: ComponentFixture<LiveViewResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
<<<<<<< HEAD
      declarations: [ LiveViewResultComponent, MockParserByParserComponent ],
=======
      declarations: [
        LiveViewResultComponent,
        FakeStackTraceComponent,
      ],
>>>>>>> BUG-123315 Adding initial version of stack trace view
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
