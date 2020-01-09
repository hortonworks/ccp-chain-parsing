import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import * as fromActions from './chain-list-page.actions';

import { ChainModel } from './chain.model';

@Injectable()
export class ChainListEffects {
  constructor(
    private actions$: Actions,
    private messageService: NzMessageService
  ) { }

  @Effect()
  loadChains$: Observable<Action> = this.actions$.pipe(
    ofType(fromActions.LOAD_CHAINS),
    switchMap((action: fromActions.LoadChainsAction) => {
      return of([
        { id: 'dummychainA', name: 'Dummy Chain A' },
        { id: 'dummychainB', name: 'Dummy Chain B' },
      ])
        .pipe(
          map((chains: ChainModel[]) => {
            return new fromActions.LoadChainsSuccessAction(chains);
          }),
          catchError((error: { message: string }) => {
            this.messageService.create('error', error.message);
            return of(new fromActions.LoadChainsFailAction(error));
          })
        );
    })
  );

  // TODO impl dummy create chain

  @Effect()
  deleteChain$: Observable<Action> = this.actions$.pipe(
    ofType(fromActions.DELETE_CHAIN),
    switchMap((action: fromActions.DeleteChainAction) => {
      return of([
        { id: 'dummychainA', name: 'Dummy Chain A' },
      ])
        .pipe(
          map((chains: ChainModel[]) => {
            return new fromActions.DeleteChainSuccessAction(chains);
          }),
          catchError((error: { message: string }) => {
            this.messageService.create('error', error.message);
            return of(new fromActions.DeleteChainFailAction(error));
          })
        );
    })
  );
}
