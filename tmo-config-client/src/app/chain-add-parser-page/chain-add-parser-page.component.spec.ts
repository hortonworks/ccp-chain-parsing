import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { StoreModule } from '@ngrx/store';
import { NgZorroAntdModule } from 'ng-zorro-antd';
import { of } from 'rxjs';

import { ChainAddParserPageComponent } from './chain-add-parser-page.component';
import { reducer } from './chain-add-parser-page.reducers';

const fakeActivatedRoute = {
  params: of({})
};

describe('ChainAddParserPageComponent', () => {
  let component: ChainAddParserPageComponent;
  let fixture: ComponentFixture<ChainAddParserPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChainAddParserPageComponent ],
      imports: [
        NgZorroAntdModule,
        FormsModule,
        ReactiveFormsModule,
        NoopAnimationsModule,
        StoreModule.forRoot({
          'chain-add-parser-page': reducer
        }),
        RouterTestingModule
      ],
      providers: [
        { provide: ActivatedRoute, useFactory: () => fakeActivatedRoute }
      ]
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
