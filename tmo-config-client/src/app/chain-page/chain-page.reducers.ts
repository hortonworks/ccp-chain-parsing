import { createSelector } from '@ngrx/store';

import * as chainPageActions from './chain-page.actions';
import { ParserChainModel, ParserModel, RouteModel } from './chain-page.models';

export interface ChainPageState {
  chains: { [key: string]: ParserChainModel };
  parsers: { [key: string]: ParserModel };
  routes: { [key: string]: RouteModel };
  error: string;
}

export const initialState: ChainPageState = {
  chains: {},
  parsers: {},
  routes: {},
  error: '',
};

export function reducer(
  state: ChainPageState = initialState,
  action: chainPageActions.ChainDetailsAction
): ChainPageState {
  switch (action.type) {
    case chainPageActions.LOAD_CHAIN_DETAILS_SUCCESS: {
      return {
        ...state,
        chains: action.payload.chains,
        parsers: action.payload.parsers,
        routes: action.payload.routes
      };
    }
    case chainPageActions.REMOVE_PARSER: {
      return {
        ...state,
        chains: {
          ...state.chains,
          [action.payload.chainId]: {
            ...state.chains[action.payload.chainId],
            parsers: (state.chains[action.payload.chainId].parsers as string[])
              .filter((parserId: string) => parserId !== action.payload.id)
          }
        }
      };
    }
    case chainPageActions.UPDATE_PARSER: {
      return {
        ...state,
        parsers: {
          ...state.parsers,
          [action.payload.parser.id]: {
            ...state.parsers[action.payload.parser.id],
            ...action.payload.parser
          }
        }
      };
    }
  }
  return state;
}

export function getChainPageState(state: any): ChainPageState {
  return state['chain-page'];
}

export const getChains = createSelector(
  getChainPageState,
  (state: ChainPageState): { [key: string]: ParserChainModel } => {
    return state.chains;
  }
);

export const getChain = createSelector(
  getChainPageState,
  (state, props): ParserChainModel => {
    return state.chains[props.id];
  }
);

export const getParser = createSelector(
  getChainPageState,
  (state, props): ParserModel => {
    return state.parsers[props.id];
  }
);

export const getRoute = createSelector(
  getChainPageState,
  (state, props): RouteModel => {
    return state.routes[props.id];
  }
);
