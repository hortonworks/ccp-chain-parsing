import { createSelector } from '@ngrx/store';

import { ChainDetailsModel } from './chain-details.model';
import * as chainPageActions from './chain-page.actions';

export interface ChainPageState {
  details: ChainDetailsModel;
  error: string;
}

export const initialState: ChainPageState = {
  details: {
    id: '',
    name: '',
    parsers: []
  },
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
        details: {
          ...state.details,
          ...action.payload
        }
      };
    }
    case chainPageActions.REMOVE_PARSER: {
      return {
        ...state,
        details: {
          ...state.details,
          parsers: state.details.parsers.filter(p => p.id !== action.payload.id)
        }
      };
    }
  }
  return state;
}

function getChainPageState(state: any): ChainPageState {
  return state['chain-page'];
}

export const getChainDetails = createSelector(
  getChainPageState,
  (state: ChainPageState) => state.details
);
