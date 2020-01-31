import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { ChainPageService } from './../services/chain-page.service';
import { ChainDetailsModel } from './chain-details.model';
import * as fromActions from './chain-page.actions';
import { normalizeParserConfig } from './chain-page.utils';

@Injectable()
export class ChainPageEffects {
  constructor(
    private actions$: Actions,
    private messageService: NzMessageService,
    private chainPageService: ChainPageService
  ) {}

  @Effect()
  loadChainDetails$: Observable<Action> = this.actions$.pipe(
    ofType(fromActions.LOAD_CHAIN_DETAILS),
    switchMap((action: fromActions.LoadChainDetailsAction) => {
      return this.chainPageService.getChain(action.payload.id).pipe(
        map((chain: ChainDetailsModel) => {
          const normalizedParserConfig = normalizeParserConfig(chain);
          return new fromActions.LoadChainDetailsSuccessAction(
            normalizedParserConfig
          );
        }),
        catchError((error: { message: string }) => {
          throw error;
          this.messageService.create('error', error.message);
          return of(new fromActions.LoadChainDetailsFailAction(error));
        })
      );
    })
  );
}
