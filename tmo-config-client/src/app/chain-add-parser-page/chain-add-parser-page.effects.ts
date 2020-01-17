import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { NzMessageService } from 'ng-zorro-antd';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { ParserModel } from '../chain-page/chain-details.model';
import { AddParserPageService } from '../services/chain-add-parser-page.service';

import * as fromActions from './chain-add-parser-page.actions';

@Injectable()
export class AddParserEffects {
  constructor(
    private actions$: Actions,
    private messageService: NzMessageService,
    private addParserService: AddParserPageService,
    private router: Router
  ) { }

  @Effect()
  addParser$: Observable<Action> = this.actions$.pipe(
    ofType(fromActions.ADD_PARSER),
    switchMap((action: fromActions.AddParserAction) => {
      return this.addParserService.add(
        action.payload.chainId,
        action.payload.parser
      )
        .pipe(
          map((parser: ParserModel) => {
            this.router.navigateByUrl(`/parserconfig/chains/${action.payload.chainId}`);
            return new fromActions.AddParserSuccessAction({
              chainId: action.payload.chainId,
              parser
            });
          }),
          catchError((error: { message: string }) => {
            this.messageService.create('error', error.message);
            return of(new fromActions.AddParserFailAction(error));
          })
        );
    })
  );

  @Effect()
  getParserTypes$: Observable<Action> = this.actions$.pipe(
    ofType(fromActions.GET_PARSER_TYPES),
    switchMap(() => {
      return this.addParserService.getParserTypes()
        .pipe(
          map((parserTypes: { id: string, name: string }[]) => {
            return new fromActions.GetParserTypesSuccessAction(parserTypes);
          }),
          catchError((error: { message: string }) => {
            this.messageService.create('error', error.message);
            return of(new fromActions.GetParserTypesFailAction(error));
          })
        );
    })
  );

  @Effect()
  getParsers$: Observable<Action> = this.actions$.pipe(
    ofType(fromActions.GET_PARSERS),
    switchMap((action: fromActions.GetParsersAction) => {
      return this.addParserService.getParsers(action.payload.chainId)
        .pipe(
          map((parsers: ParserModel[]) => {
            return new fromActions.GetParsersSuccessAction(parsers);
          }),
          catchError((error: { message: string }) => {
            this.messageService.create('error', error.message);
            return of(new fromActions.GetParsersFailAction(error));
          })
        );
    })
  );
}
