import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { Observable } from 'rxjs';
import { debounceTime, map } from 'rxjs/operators';


import { chainConfigChanged, LiveViewActionsType, refreshTick } from './live-view.actions';

export const LIVE_VIEW_DEBOUNCE_RATE = 1000;

@Injectable()
export class LiveViewEffects {
  constructor(
    private actions$: Actions<LiveViewActionsType>,
    private messageService: NzMessageService,
  ) {}

  @Effect()
  chainConfigChanged$: Observable<Action> = this.actions$.pipe(
    ofType(chainConfigChanged.type),
    debounceTime(LIVE_VIEW_DEBOUNCE_RATE),
    map(action => refreshTick({ chainConfig: action.chainConfig })),
  );

  // @Effect()
  // refresh$: Observable<Action> = this.actions$.pipe(
  //   ofType(refreshTick.type),
  //   switchMap((action: fromActions.LoadChainDetailsAction) => {
  //     return this.chainPageService.getParsers(action.payload.id).pipe(
  //       map((details: ChainDetailsModel) => {
  //         return new fromActions.LoadChainDetailsSuccessAction(details);
  //       }),
  //       catchError((error: { message: string }) => {
  //         this.messageService.create('error', error.message);
  //         return of(new fromActions.LoadChainDetailsFailAction(error));
  //       })
  //     );
  //   })
  // );
}
