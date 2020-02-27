import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DeleteFill } from '@ant-design/icons-angular/icons';
import { NgZorroAntdModule, NZ_ICONS } from 'ng-zorro-antd';
import { MonacoEditorModule } from 'ngx-monaco-editor';

import { ParserComponent } from './parser.component';

@Component({
  selector: 'app-custom-form',
  template: '',
})
export class MockCustomFormComponent {
  @Input() config = [];
}

describe('ParserComponent', () => {
  let component: ParserComponent;
  let fixture: ComponentFixture<ParserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        MonacoEditorModule.forRoot({
          onMonacoLoad() {
            monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
              validate: true,
              schemas: []
            });
          }
        }),
      ],
      declarations: [
        ParserComponent,
        MockCustomFormComponent,
      ],
      providers : [
        { provide: NZ_ICONS, useValue: [DeleteFill]}
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParserComponent);
    component = fixture.componentInstance;
    component.parser = {
      id: '123',
      name: 'Some parser',
      type: 'Bro'
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set a default value if no value in parser', () => {
    const fields = component.updateFormValues('config', [{
      id: 'foo',
      name: 'foo',
      type: 'text',
      path: 'config.a.b.c.d',
      defaultValue: 'default'
    }]);
    expect(fields[0].value).toBe('default');
  });

  it('should set a empty string if no either value in parser or defaultValue in field', () => {
    const fields = component.updateFormValues('config', [{
      id: 'foo',
      name: 'foo',
      type: 'text',
      path: 'config.a.b.c.d',
    }]);
    expect(fields[0].value).toBe('');
  });

  it('should get the value from the parser', () => {
    component.parser = {
      id: '123',
      name: 'Some parser',
      type: 'Bro',
      config: { a: { b: { c: { d: { foo: 'ABCD' } } } } }
    };
    let fields = component.updateFormValues('config', [{
      id: 'foo',
      name: 'foo',
      type: 'text',
      path: 'config.a.b.c.d',
    }]);
    expect(fields[0].value).toBe('ABCD');

    component.parser = {
      id: '123',
      name: 'Some parser',
      type: 'Bro',
      config: { foo: 'ABCD' }
    };
    fields = component.updateFormValues('config', [{
      id: 'foo',
      name: 'foo',
      type: 'text',
      path: 'config',
    }]);
    expect(fields[0].value).toEqual('ABCD');

    component.parser = {
      id: '123',
      name: 'Some parser',
      type: 'Bro',
      config: {
        outputFields: {
          fieldName: 'field name',
          fieldIndex: 'field index'
        }
      }
    };
    fields = component.updateFormValues('config', [{
      id: 'fieldName',
      name: 'fieldName',
      type: 'text',
      path: 'config.outputFields',
    }, {
      id: 'fieldIndex',
      name: 'fieldIndex',
      type: 'text',
      path: 'config.outputFields',
    }]);
    expect(fields[0].value).toEqual('field name');
    expect(fields[1].value).toEqual('field index');
  });

  it('should update values in the parser properly', () => {
    component.configForm = [{
      id: 'fieldName',
      name: 'fieldName',
      type: 'text',
      path: 'config.outputFields',
      value: 'field name UPDATED'
    }, {
      id: 'fieldIndex',
      name: 'fieldIndex',
      type: 'text',
      path: 'config.outputFields',
      value: 'field index UPDATED'
    }];
    component.parser = {
      id: '123',
      name: 'Some parser',
      type: 'Bro',
      config: {
        outputFields: {
          fieldName: 'field name',
          fieldIndex: 'field index'
        }
      }
    };
    component.ngOnInit();
    component.parserChange.subscribe(() => {
      expect(component.parser.config.outputFields.fieldName).toBe('field name UPDATED');
      expect(component.parser.config.outputFields.fieldIndex).toBe('field index');
    }).unsubscribe();
    component.configForm[0].onChange(component.configForm[0]);

    component.parserChange.subscribe(() => {
      expect(component.parser.config.outputFields.fieldName).toBe('field name UPDATED');
      expect(component.parser.config.outputFields.fieldIndex).toBe('field index UPDATED');
    }).unsubscribe();
    component.configForm[0].onChange(component.configForm[0]);
    component.configForm[1].onChange(component.configForm[1]);
  });

  it('should setup the form fields properly', () => {
    component.parser = {
      id: '123',
      name: 'Some parser',
      type: 'Bro',
      config: {
        outputFields: {
          fieldName: 'field name INIT',
          fieldIndex: 'field index INIT'
        }
      }
    };
    const fields = component.setFormFieldValues([{
      name: 'whatever',
      type: 'text',
      path: '',
    }, {
      name: 'input',
      type: 'text',
      path: 'config',
    }, {
      name: 'fieldName',
      type: 'text',
      path: 'config.outputFields',
    }, {
      name: 'fieldIndex',
      type: 'text',
      path: 'config.outputFields',
    }]);
    expect(fields[0].id).toBe('123-whatever');
    expect(fields[1].id).toBe('123-config.input');
    expect(fields[2].id).toBe('123-config.outputFields.fieldName');
    expect(fields[3].id).toBe('123-config.outputFields.fieldIndex');

    expect(fields[0].value).toBe('');
    expect(fields[1].value).toBe('');
    expect(fields[2].value).toBe('field name INIT');
    expect(fields[3].value).toBe('field index INIT');
  });
});
