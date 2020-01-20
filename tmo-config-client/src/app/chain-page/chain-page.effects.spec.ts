import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { NgZorroAntdModule } from 'ng-zorro-antd';
import { Observable, of, ReplaySubject } from 'rxjs';

import { ChainPageService } from '../services/chain-page.service';

import * as fromActions from './chain-page.actions';
import { ChainPageEffects } from './chain-page.effects';

export class MockService {}

describe('chain parser page: effects', () => {
  let effects: ChainPageEffects;
  let actions: ReplaySubject<any>;
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

  it('remove parser should call the service and return with a message on success', () => {
    service.removeParser = (): Observable<any> => of();

    const removeParserSpy = spyOn(service, 'removeParser').and.callThrough();

    actions = new ReplaySubject(1);
    actions.next(new fromActions.RemoveParserAction({
      id: '456123',
      chainId: '123'
    }));

    effects.removeParser$.subscribe(result => {
      expect(result).toEqual(new fromActions.RemoveParserSuccessAction());
    });

    expect(removeParserSpy).toHaveBeenCalledWith('456123', '123');
  });
});
