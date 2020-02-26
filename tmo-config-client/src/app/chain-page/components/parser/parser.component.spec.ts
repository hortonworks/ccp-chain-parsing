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
});
