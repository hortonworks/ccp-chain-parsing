import { createAction, props, union } from '@ngrx/store';

import { ChainDetailsModel } from '../../chain-details.model';

import { LiveViewModel } from './models/live-view.model';
import { SampleDataModel } from './models/sample-data.model';

export const sampleDataChanged = createAction(
  '[LiveView] Sample Data Changed',
  props<{ sampleData: SampleDataModel }>()
);

export const chainConfigChanged = createAction(
  '[LiveView] Parser Chain Configuration Changed',
  props<{ chainConfig: ChainDetailsModel }>()
);

export const executionTriggered = createAction(
  '[LiveView] Sample Data Parsing Triggered'
);

export const liveViewRefreshedSuccessfully = createAction(
  '[LiveView] Live View Refreshed Successfully',
  props<{ liveViewResult: LiveViewModel }>()
);

export const liveViewRefreshFailed = createAction(
  '[LiveView] Live View Refresh Failed',
  props<{ error: { message: string } }>()
);

const actions = union({
  sampleDataChanged,
  chainConfigChanged,
  executionTriggered,
  liveViewRefreshedSuccessfully,
  liveViewRefreshFailed,
});

export type LiveViewActionsType = typeof actions;
