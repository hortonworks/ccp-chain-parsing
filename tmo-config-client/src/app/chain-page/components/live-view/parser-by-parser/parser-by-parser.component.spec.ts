import { async, ComponentFixture, TestBed } from '@angular/core/testing';
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
});
