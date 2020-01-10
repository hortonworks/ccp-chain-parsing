import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChainPageComponent } from './chain-page.component';

describe('ChainPageComponent', () => {
  let component: ChainPageComponent;
  let fixture: ComponentFixture<ChainPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChainPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChainPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
