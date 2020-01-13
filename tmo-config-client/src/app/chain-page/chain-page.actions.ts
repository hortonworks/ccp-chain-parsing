import { Action } from '@ngrx/store';

import { ParserModel } from './chain-details.model';

export const LOAD_CHAIN_DETAILS = '[Chain Details] load start';
export const LOAD_CHAIN_DETAILS_SUCCESS = '[Chain Details] load success';
export const LOAD_CHAIN_DETAILS_FAIL = '[Chain Details] load fail';

export class LoadChainDetailsAction implements Action {
  readonly type = LOAD_CHAIN_DETAILS;
  constructor(public payload: { id: string }) {}
}

export class LoadChainDetailsSuccessAction implements Action {
  readonly type = LOAD_CHAIN_DETAILS_SUCCESS;
  constructor(public payload: {
    parsers: ParserModel[]
  }) {}
}

export class LoadChainDetailsFailAction implements Action {
  readonly type = LOAD_CHAIN_DETAILS_FAIL;
  constructor(public error: { message: string }) {}
}

export type ChainDetailsAction = LoadChainDetailsAction
  | LoadChainDetailsSuccessAction
  | LoadChainDetailsFailAction;
