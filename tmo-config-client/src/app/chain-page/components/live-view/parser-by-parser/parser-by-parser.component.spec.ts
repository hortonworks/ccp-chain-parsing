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

describe('ParserByParserComponent', () => {
  let component: ParserByParserComponent;
  let fixture: ComponentFixture<ParserByParserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ParserByParserComponent],
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
        input: { original_string: 'test sample' },
        output: {},
        log: {
          type: 'info',
          message: 'this is a message',
          parserId: '1234'
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
    fixture.detectChanges();

    const emptyMessage = fixture.debugElement.query(By.css('.ant-result-title'));

    expect(emptyMessage.nativeElement.textContent).toContain(component.compileErrorMessage);

    component.parserResults = [];
    fixture.detectChanges();

    expect(emptyMessage.nativeElement.textContent).toContain(component.compileErrorMessage);
  });
});
