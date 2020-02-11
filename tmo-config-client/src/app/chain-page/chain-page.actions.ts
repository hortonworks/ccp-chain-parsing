import { Action } from '@ngrx/store';

import { ParserChainModel, ParserModel, PartialParserModel, RouteModel } from './chain-page.models';

export const LOAD_CHAIN_DETAILS = '[Chain Details] load start';
export const LOAD_CHAIN_DETAILS_SUCCESS = '[Chain Details] load success';
export const LOAD_CHAIN_DETAILS_FAIL = '[Chain Details] load fail';
export const REMOVE_PARSER = '[Chain Details] remove parser';
export const UPDATE_PARSER = '[Chain Details] update parser';
export const SAVE_PARSER_CONFIG = '[Chain Details] save parser config';
export const SAVE_PARSER_CONFIG_SUCCESS = '[Chain Details] save parser config success';
export const SAVE_PARSER_CONFIG_FAIL = '[Chain Details] save parser config fail';

export class LoadChainDetailsAction implements Action {
  readonly type = LOAD_CHAIN_DETAILS;
  constructor(public payload: { id: string }) {}
}

export class LoadChainDetailsSuccessAction implements Action {
  readonly type = LOAD_CHAIN_DETAILS_SUCCESS;
  constructor(public payload: {
    chains: { [key: string]: ParserChainModel },
    routes: { [key: string]: RouteModel },
    parsers: { [key: string]: ParserModel }
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

export class UpdateParserAction implements Action {
  readonly type = UPDATE_PARSER;
  constructor(public payload: { parser: PartialParserModel }) {}
}

export class SaveParserConfigAction implements Action {
  readonly type = SAVE_PARSER_CONFIG;
  constructor(public payload: { chainId: string }) {}
}

export class SaveParserConfigSuccessAction implements Action {
  readonly type = SAVE_PARSER_CONFIG_SUCCESS;
  constructor() {}
}

export class SaveParserConfigFailAction implements Action {
  readonly type = SAVE_PARSER_CONFIG_FAIL;
  constructor(public error: { message: string }) {}
}

export type ChainDetailsAction = LoadChainDetailsAction
  | LoadChainDetailsSuccessAction
  | LoadChainDetailsFailAction
  | RemoveParserAction
  | UpdateParserAction
  | SaveParserConfigAction
  | SaveParserConfigFailAction
  | SaveParserConfigFailAction;
