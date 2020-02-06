import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { NgZorroAntdModule } from 'ng-zorro-antd';
import { of, Observable } from 'rxjs';

import { ParserModel } from './chain-details.model';
import { ChainPageComponent } from './chain-page.component';
import * as fromReducers from './chain-page.reducers';

@Component({
  selector: 'app-chain-view',
  template: ''
})
class MockChainViewComponent {
  @Input() parsers: ParserModel[];
}

@Component({
  selector: 'app-live-view',
  template: ''
})
class MockLiveViewComponent {
  @Input() chainConfig$: Observable<{}>;
}

const fakeActivatedRoute = {
  params: of({})
};

describe('ChainPageComponent', () => {
  let component: ChainPageComponent;
  let fixture: ComponentFixture<ChainPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        StoreModule.forRoot({
          'chain-page': fromReducers.reducer
        })
      ],
      declarations: [ChainPageComponent, MockChainViewComponent, MockLiveViewComponent],
      providers: [
        { provide: ActivatedRoute, useFactory: () => fakeActivatedRoute }
      ]
    }).compileComponents();
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
