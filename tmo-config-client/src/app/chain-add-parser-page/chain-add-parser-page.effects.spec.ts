import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { NgZorroAntdModule } from 'ng-zorro-antd';
import { Observable, of, ReplaySubject } from 'rxjs';

import { AddParserPageService } from '../services/chain-add-parser-page.service';

import * as fromActions from './chain-add-parser-page.actions';
import { AddParserEffects } from './chain-add-parser-page.effects';

export class MockService {}

describe('chain add parser page: effects', () => {
  let effects: AddParserEffects;
  let actions: ReplaySubject<any>;
  let service: AddParserPageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        AddParserEffects,
        provideMockActions(() => actions),
        { provide: AddParserPageService, useClass: MockService },
      ]
    });

    effects = TestBed.get(AddParserEffects);
    service = TestBed.get(AddParserPageService);
  });

  it('add parser should call the service and return with the newly added parser when it succeeds', () => {
    const expected = {
      chainId: '123',
      parser: {
        id: '456',
        name: 'foo',
        type: 'bar'
      }
    };

    service.add = (): Observable<any> => of(expected.parser);

    const spy = spyOn(service, 'add').and.callThrough();

    actions = new ReplaySubject(1);
    actions.next(new fromActions.AddParserAction({
      chainId: '123',
      parser: {
        id: '456',
        name: 'foo',
        type: 'bar'
      }
    }));

    effects.addParser$.subscribe(result => {
      expect(result).toEqual(new fromActions.AddParserSuccessAction(expected));
    });

    expect(spy).toHaveBeenCalledWith(expected.chainId, expected.parser);
  });

  it('get parser types should call the service and return with the parser types when it succeeds', () => {
    const expected = [{
      id: '1',
      name: 'foo'
    }, {
      id: '2',
      name: 'bar'
    }];

    service.getParserTypes = (): Observable<any> => of(expected);

    const spy = spyOn(service, 'getParserTypes').and.callThrough();

    actions = new ReplaySubject(1);
    actions.next(new fromActions.GetParserTypesAction());

    effects.getParserTypes$.subscribe(result => {
      expect(result).toEqual(new fromActions.GetParserTypesSuccessAction(expected));
    });

    expect(spy).toHaveBeenCalledWith();
  });

  it('get parsers should call the service and return with the parser types when it succeeds', () => {
    const expected = [{
      id: '1',
      name: 'foo',
      type: 'lorem'
    }, {
      id: '2',
      name: 'bar',
      type: 'ipsum'
    }];

    service.getParsers = (): Observable<any> => of(expected);

    const spy = spyOn(service, 'getParsers').and.callThrough();

    actions = new ReplaySubject(1);
    actions.next(new fromActions.GetParsersAction({
      chainId: '123'
    }));

    effects.getParsers$.subscribe(result => {
      expect(result).toEqual(new fromActions.GetParsersSuccessAction(expected));
    });

    expect(spy).toHaveBeenCalledWith('123');
  });

});
