import { Component, forwardRef, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NgZorroAntdModule } from 'ng-zorro-antd';

import { ParserModel } from '../../chain-details.model';
import { ParserComponent } from '../parser/parser.component';
import { RouterComponent } from '../router/router.component';

import { ChainViewComponent } from './chain-view.component';


@Component({
  // tslint:disable-next-line:component-selector
  selector: 'ngx-monaco-editor',
  template: '<input>',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => MockMonacoEditorComponent),
    multi: true
  }]
})

class MockMonacoEditorComponent implements ControlValueAccessor {
  @Input() options: any;

  writeValue = () => {};
  propagateChange = (_: any) => {};
  onTouched = () => {};
  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
}

describe('ChainViewComponent', () => {
  let component: ChainViewComponent;
  let fixture: ComponentFixture<ChainViewComponent>;
  const parsers: ParserModel[] = [
    {
      id: '123',
      name: 'Syslog',
      type: 'Grok',
      config: {},
      input: '',
      outputs: ''
    }
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ NgZorroAntdModule, NoopAnimationsModule ],
      declarations: [
        ChainViewComponent,
        MockMonacoEditorComponent,
        ParserComponent,
        RouterComponent
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChainViewComponent);
    component = fixture.componentInstance;
    component.parsers = parsers;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit an event when delete parser is clicked and confirmed', () => {
    const deleteBtn = fixture.nativeElement.querySelector('[data-qe-id="remove-parser"]');
    const removeParserEmitSpy = spyOn(component.removeParserEmitter, 'emit');

    deleteBtn.click();
    fixture.detectChanges();

    const confirmBtn = document.querySelector('.ant-popover .ant-btn-primary') as HTMLElement;
    confirmBtn.click();
    expect(removeParserEmitSpy).toHaveBeenCalledWith('123');
  });
});
