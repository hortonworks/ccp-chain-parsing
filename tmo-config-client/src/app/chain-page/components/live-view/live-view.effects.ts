import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { combineLatest, Observable, of } from 'rxjs';
import { catchError, debounceTime, filter, map, switchMap, take } from 'rxjs/operators';


import { chainConfigChanged, executionTriggered, LiveViewActionsType, liveViewRefreshedSuccessfully, liveViewRefreshFailed, sampleDataChanged } from './live-view.actions';
import { LiveViewState } from './live-view.reducers';
import { getChainConfig, getSampleData } from './live-view.selectors';
import { LiveViewService } from './services/live-view.service';

export const LIVE_VIEW_DEBOUNCE_RATE = 1000;

@Injectable()
export class LiveViewEffects {
  constructor(
    private store: Store<LiveViewState>,
    private actions$: Actions<LiveViewActionsType>,
    private liveViewService: LiveViewService,
    private messageService: NzMessageService,
  ) {}

  @Effect()
  liveViewConfigChanged$: Observable<Action> = this.actions$.pipe(
    ofType(
      chainConfigChanged.type,
      sampleDataChanged.type,
    ),
    // filter(action => action.type === sampleDataChanged.type && !!action.sampleData.source ),
    debounceTime(LIVE_VIEW_DEBOUNCE_RATE),
    switchMap(() => {
      return combineLatest(
        this.store.pipe(select(getSampleData)),
        this.store.pipe(select(getChainConfig)),
      ).pipe(take(1));
    }),
    filter(([ sampleData, chainConfig ]) => !!sampleData.source),
    map(([ sampleData, chainConfig ]) => executionTriggered({ sampleData, chainConfig })),
  );

  @Effect()
  execute$: Observable<Action> = this.actions$.pipe(
    ofType(
      executionTriggered.type,
    ),
    switchMap(({ sampleData, chainConfig }) => {
      return this.liveViewService.execute(sampleData, chainConfig).pipe(
        map(liveViewResult => liveViewRefreshedSuccessfully({ liveViewResult })),
        catchError(( error: { message: string }) => {
          this.messageService.create('error', error.message);
          return of(liveViewRefreshFailed({ error }));
        })
      ); })
  );
}
