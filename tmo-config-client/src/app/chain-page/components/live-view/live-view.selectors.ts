import { createSelector } from '@ngrx/store';

import { ChainPageState, getChainDetails } from '../../chain-page.reducers';

import { LiveViewState } from './live-view.reducers';

function getLiveViewState(state: any): LiveViewState {
  return state['live-view'];
}

export const getChainConfig = createSelector(
  getChainDetails,
  (state: ChainPageState) => state
);

export const getSampleData = createSelector(
  getLiveViewState,
  (state: LiveViewState) => state.sampleData
);

export const getExecutionStatus = createSelector(
  getLiveViewState,
  (state: LiveViewState) => state.isExecuting
);
