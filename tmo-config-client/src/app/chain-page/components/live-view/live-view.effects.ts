import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, Store, select } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { Observable, forkJoin, of, zip, combineLatest } from 'rxjs';
import { debounceTime, map, switchMap, catchError, filter, tap, take } from 'rxjs/operators';


import { chainConfigChanged, LiveViewActionsType, sampleDataChanged, liveViewRefreshedSuccessfully, liveViewRefreshFailed, executionTriggered } from './live-view.actions';
import { LiveViewService } from './services/live-view.service';
import { LiveViewModel } from './models/live-view.model';
import { getChainConfig, getSampleData } from './live-view.selectors';
import { LiveViewState } from './live-view.reducers';

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
    filter(action => action.type === sampleDataChanged.type && !!action.sampleData.source ),
    debounceTime(LIVE_VIEW_DEBOUNCE_RATE),
    map(() => executionTriggered()),
  );

  @Effect()
  execute$: Observable<Action> = this.actions$.pipe(
    ofType(
      executionTriggered.type,
    ),
    switchMap(() => {
      return combineLatest(
        this.store.pipe(select(getSampleData)),
        this.store.pipe(select(getChainConfig)),
      ).pipe(take(1));
    }),
    switchMap(([ sampleData, chainConfig ]) => {
      return this.liveViewService.execute(sampleData, chainConfig).pipe(
        map(liveViewResult => liveViewRefreshedSuccessfully({ liveViewResult })),
        catchError(( error: { message: string }) => {
          this.messageService.create('error', error.message);
          return of(liveViewRefreshFailed({ error }));
        })
      )})
  );
}
