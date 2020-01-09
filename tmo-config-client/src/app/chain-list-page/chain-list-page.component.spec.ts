import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { StoreModule } from '@ngrx/store';
import { NgZorroAntdModule } from 'ng-zorro-antd';

import { ChainListPageComponent } from './chain-list-page.component';
import * as fromReducers from './chain-list-page.reducers';

describe('ChainListPageComponent', () => {
  let component: ChainListPageComponent;
  let fixture: ComponentFixture<ChainListPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        StoreModule.forRoot({
          'chain-list-page': fromReducers.reducer
        })
      ],
      declarations: [ChainListPageComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChainListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
