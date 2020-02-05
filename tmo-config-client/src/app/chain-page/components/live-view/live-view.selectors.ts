import { createSelector } from '@ngrx/store';

import { ChainPageState } from '../../chain-page.reducers';

import { LiveViewState } from './live-view.reducers';

function getChainPageState(state: any): ChainPageState {
  return state['chain-page'];
}

function getLiveViewState(state: any): LiveViewState {
  return state['live-view'];
}

export const getChainConfig = createSelector(
  getChainPageState,
  (state: ChainPageState) => state.details
);

export const getSampleData = createSelector(
  getLiveViewState,
  (state: LiveViewState) => state.sampleData
);

export const getExecutionStatus = createSelector(
  getLiveViewState,
  (state: LiveViewState) => state.isExecuting
);
