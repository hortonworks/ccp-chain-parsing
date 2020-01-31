import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ParserComposerComponent } from './parser-composer.component';

describe('ParserComposerComponent', () => {
  let component: ParserComposerComponent;
  let fixture: ComponentFixture<ParserComposerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParserComposerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParserComposerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
