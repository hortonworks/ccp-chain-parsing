import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { NgZorroAntdModule } from 'ng-zorro-antd';
import { ReplaySubject } from 'rxjs';

import { ChainPageService } from '../services/chain-page.service';

import { ChainPageEffects } from './chain-page.effects';

export class MockService {}

describe('chain parser page: effects', () => {
  const actions: ReplaySubject<any> = new ReplaySubject();
  let effects: ChainPageEffects;
  let service: ChainPageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        ChainPageEffects,
        provideMockActions(() => actions),
        { provide: ChainPageService, useClass: MockService },
      ]
    });

    effects = TestBed.get(ChainPageEffects);
    service = TestBed.get(ChainPageService);
  });
});
