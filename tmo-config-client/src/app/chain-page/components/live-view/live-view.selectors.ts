import { createSelector } from '@ngrx/store';

import { ChainPageState } from '../../chain-page.reducers';

function getChainPageState(state: any): ChainPageState {
  return state['chain-page'];
}

export const getChainConfig = createSelector(
  getChainPageState,
  (state: ChainPageState) => state.details
);
