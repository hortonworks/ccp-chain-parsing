import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChainAddParserPageComponent } from './chain-add-parser-page.component';

describe('ChainAddParserPageComponent', () => {
  let component: ChainAddParserPageComponent;
  let fixture: ComponentFixture<ChainAddParserPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChainAddParserPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChainAddParserPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
