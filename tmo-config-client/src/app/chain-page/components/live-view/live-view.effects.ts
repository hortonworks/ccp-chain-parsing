import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import {
  executionTriggered,
  LiveViewActionsType,
  liveViewInitialized,
  liveViewRefreshedSuccessfully,
  liveViewRefreshFailed,
  onOffToggleRestored,
  sampleDataRestored,
} from './live-view.actions';
import { LiveViewConsts } from './live-view.consts';
import { LiveViewService } from './services/live-view.service';

@Injectable()
export class LiveViewEffects {
  constructor(
    private actions$: Actions<LiveViewActionsType>,
    private liveViewService: LiveViewService,
    private messageService: NzMessageService,
  ) {}

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
      );
    })
  );

  @Effect()
  restoreSampleDataFromLocalStore: Observable<Action> = this.actions$.pipe(
    ofType(
      liveViewInitialized.type,
    ),
    switchMap(() => {
      const value = JSON.parse(localStorage.getItem(LiveViewConsts.SAMPLE_DATA_STORAGE_KEY));
      return of(sampleDataRestored(value));
    })
  );

  @Effect()
  restoreToggleFromLocalStore: Observable<Action> = this.actions$.pipe(
    ofType(
      liveViewInitialized.type,
    ),
    switchMap(() => {
      const value = JSON.parse(localStorage.getItem(LiveViewConsts.FEATURE_TOGGLE_STORAGE_KEY));
      return of(onOffToggleRestored(value));
    })
  );
}
