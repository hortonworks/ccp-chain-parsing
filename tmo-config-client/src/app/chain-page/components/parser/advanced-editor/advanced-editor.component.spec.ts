import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvancedEditorComponent } from './advanced-editor.component';

describe('AdvancedEditorComponent', () => {
  let component: AdvancedEditorComponent;
  let fixture: ComponentFixture<AdvancedEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdvancedEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdvancedEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
