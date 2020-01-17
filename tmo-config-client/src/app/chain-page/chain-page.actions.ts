import { Action } from '@ngrx/store';

import { ParserModel } from './chain-details.model';

export const LOAD_CHAIN_DETAILS = '[Chain Details] load start';
export const LOAD_CHAIN_DETAILS_SUCCESS = '[Chain Details] load success';
export const LOAD_CHAIN_DETAILS_FAIL = '[Chain Details] load fail';
export const REMOVE_PARSER = '[Chain Details] remove parser start';
export const REMOVE_PARSER_SUCCESS = '[Chain Details] remove parser success';
export const REMOVE_PARSER_FAIL = '[Chain Details] remove parser fail';

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

export class RemoveParserAction implements Action {
  readonly type = REMOVE_PARSER;
  constructor(public payload: { id: string, chainId: string }) {}
}

export class RemoveParserSuccessAction implements Action {
  readonly type = REMOVE_PARSER_SUCCESS;
  constructor(public payload?) {}
}

export class RemoveParserFailAction implements Action {
  readonly type = REMOVE_PARSER_FAIL;
  constructor(public payload?) {}
}

export type ChainDetailsAction = LoadChainDetailsAction
  | LoadChainDetailsSuccessAction
  | LoadChainDetailsFailAction
  | RemoveParserAction
  | RemoveParserSuccessAction
  | RemoveParserFailAction;
