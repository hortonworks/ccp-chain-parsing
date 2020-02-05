import { createSelector } from '@ngrx/store';

import { ParserModel } from '../chain-page/chain-page.models';

import * as addParserActions from './chain-add-parser-page.actions';

export interface AddParserPageState {
  parserTypes: { id: string, name: string }[];
  parsers: ParserModel[];
  error: string;
}

export const initialState: AddParserPageState = {
  parserTypes: [],
  parsers: [],
  error: '',
};

export function reducer(
  state: AddParserPageState = initialState,
  action: addParserActions.ParserAction
): AddParserPageState {
  switch (action.type) {
    case addParserActions.GET_PARSER_TYPES_SUCCESS: {
      return {
        ...state,
        parserTypes: action.payload
      };
    }
    case addParserActions.GET_PARSERS_SUCCESS: {
      return {
        ...state,
        parsers: action.payload
      };
    }
  }
  return state;
}

function getChainAddParserPageState(state: any): AddParserPageState {
  return state['chain-add-parser-page'];
}

export const getParserTypes = createSelector(
  getChainAddParserPageState,
  (state: AddParserPageState) => state.parserTypes
);

export const getParsers = createSelector(
  getChainAddParserPageState,
  (state: AddParserPageState) => state.parsers
);
