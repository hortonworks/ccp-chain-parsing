import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgZorroAntdModule } from 'ng-zorro-antd';
import { MonacoEditorModule } from 'ngx-monaco-editor';

import { RouterComponent } from './router.component';

describe('RouterComponent', () => {
  let component: RouterComponent;
  let fixture: ComponentFixture<RouterComponent>;

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
      declarations: [ RouterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RouterComponent);
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
