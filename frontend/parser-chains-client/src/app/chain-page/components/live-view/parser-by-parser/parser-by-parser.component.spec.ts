import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { CheckCircleOutline, CloseCircleOutline, WarningFill } from '@ant-design/icons-angular/icons';
import {
  NZ_ICONS,
  NzCardModule,
  NzResultModule,
  NzTimelineModule
} from 'ng-zorro-antd';

import { ParserByParserComponent } from './parser-by-parser.component';

@Component({
  selector: 'app-stack-trace',
  template: '',
})
class FakeStackTraceComponent {
  @Input() stackTraceMsg = '';
}

describe('ParserByParserComponent', () => {
  let component: ParserByParserComponent;
  let fixture: ComponentFixture<ParserByParserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ParserByParserComponent,
        FakeStackTraceComponent,
      ],
      imports: [NzCardModule, NzTimelineModule, NzResultModule],
      providers: [
        {
          provide: NZ_ICONS,
          useValue: [CheckCircleOutline, CloseCircleOutline, WarningFill]
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParserByParserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit parser id to investigate when clicked on', () => {
    const investigatorSpy = spyOn(component.investigateParser, 'emit');

    component.parserResults = [
      {
        output: {},
        log: {
          type: 'info',
          message: 'this is a message',
          parserId: '1234',
          stackTrace: 'Fake Strack Trace Msg',
        }
      }
    ];

    fixture.detectChanges();
    const investigateParserBtn = fixture.debugElement.query(By.css('[data-qe-id="investigateParserBtn"]'));
    investigateParserBtn.nativeElement.click();
    expect(investigatorSpy).toHaveBeenCalledWith('1234');
  });

  it('should display empty message when parserResults is not returned', () => {
    component.parserResults = null;
    component.logMessage = 'this is a test error message';
    fixture.detectChanges();

    const emptyMessage = fixture.debugElement.query(By.css('.ant-result-title'));
    const logMessage = fixture.debugElement.query(By.css('[data-qe-id="logMessage"'));

    expect(emptyMessage.nativeElement.textContent).toContain(component.compileErrorDescription);

    component.parserResults = [];
    fixture.detectChanges();

    expect(emptyMessage.nativeElement.textContent).toContain(component.compileErrorDescription);
    expect(logMessage.nativeElement.textContent).toContain(component.logMessage);
  });

  it('should bind stack trace msg to stack trace component', () => {
    component.parserResults = [
      {
        output: {},
        log: {
          type: 'info',
          message: 'this is a message',
          parserId: '1234',
          stackTrace: 'Fake Strack Trace Msg',
        }
      }
    ];
    fixture.detectChanges();

    const stackTraceComp = fixture.debugElement.query(
      By.directive(FakeStackTraceComponent)
      ).componentInstance;

    expect(stackTraceComp.stackTraceMsg).toBe('Fake Strack Trace Msg');
  });
});
