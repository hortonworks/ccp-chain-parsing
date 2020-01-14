import { Action } from '@ngrx/store';

import { ParserModel } from '../chain-page/chain-details.model';

export const ADD_PARSER = '[Chain Add Parser] add start';
export const ADD_PARSER_SUCCESS = '[Chain Add Parser] add success';
export const ADD_PARSER_FAIL = '[Chain Add Parser] add fail';
export const GET_PARSER_TYPES = '[Chain Add Parser] get parser types';
export const GET_PARSER_TYPES_SUCCESS = '[Chain Add Parser] get parser types success';
export const GET_PARSER_TYPES_FAIL = '[Chain Add Parser] get parser types fail';
export const GET_PARSERS = '[Chain Add Parser] get parsers';
export const GET_PARSERS_SUCCESS = '[Chain Add Parser] get parsers success';
export const GET_PARSERS_FAIL = '[Chain Add Parser] get parsersfail';

export class AddParserAction implements Action {
  readonly type = ADD_PARSER;
  constructor(public payload: {
    chainId: string,
    parser: ParserModel
  }) {}
}

export class AddParserSuccessAction implements Action {
  readonly type = ADD_PARSER_SUCCESS;
  constructor(public payload: {
    chainId: string,
    parser: ParserModel
  }) {}
}

export class AddParserFailAction implements Action {
  readonly type = ADD_PARSER_FAIL;
  constructor(public error: { message: string }) {}
}

export class GetParserTypesAction implements Action {
  readonly type = GET_PARSER_TYPES;
  constructor() {}
}

export class GetParserTypesSuccessAction implements Action {
  readonly type = GET_PARSER_TYPES_SUCCESS;
  constructor(public payload: { id: string, name: string }[]) {}
}

export class GetParserTypesFailAction implements Action {
  readonly type = GET_PARSER_TYPES_FAIL;
  constructor(public error: { message: string }) {}
}

export class GetParsersAction implements Action {
  readonly type = GET_PARSERS;
  constructor(public payload: { chainId: string }) {}
}

export class GetParsersSuccessAction implements Action {
  readonly type = GET_PARSERS_SUCCESS;
  constructor(public payload: ParserModel[]) {}
}

export class GetParsersFailAction implements Action {
  readonly type = GET_PARSERS_FAIL;
  constructor(public error: { message: string }) {}
}

export type ParserAction = AddParserAction
  | AddParserSuccessAction
  | AddParserFailAction
  | GetParserTypesAction
  | GetParserTypesSuccessAction
  | GetParserTypesFailAction
  | GetParserTypesFailAction
  | GetParsersAction
  | GetParsersSuccessAction
  | GetParsersFailAction;
