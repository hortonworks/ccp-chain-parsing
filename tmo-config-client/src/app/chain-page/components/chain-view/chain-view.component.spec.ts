import { Component, forwardRef, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { NgZorroAntdModule } from 'ng-zorro-antd';

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

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ NgZorroAntdModule ],
      declarations: [ ChainViewComponent, MockMonacoEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChainViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
