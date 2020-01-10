import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { ChainPageService } from './../services/chain-page.service';
import { ChainDetailsModel } from './chain-details.model';
import * as fromActions from './chain-page.actions';

@Injectable()
export class ChainPageEffects {
  constructor(
    private actions$: Actions,
    private messageService: NzMessageService,
    private chainPageService: ChainPageService
  ) { }

  @Effect()
  loadChainDetails$: Observable<Action> = this.actions$.pipe(
    ofType(fromActions.LOAD_CHAIN_DETAILS),
    switchMap((action: fromActions.LoadChainDetailsAction) => {
      return this.chainPageService.getParsers(
        action.payload.id
      )
        .pipe(
          map((details: ChainDetailsModel) => {
            return new fromActions.LoadChainDetailsSuccessAction(details);
          }),
          catchError((error: { message: string }) => {
            this.messageService.create('error', error.message);
            return of(new fromActions.LoadChainDetailsFailAction(error));
          })
        );
    })
  );
}
