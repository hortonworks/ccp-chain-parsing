import { createAction, props, union } from '@ngrx/store';

import { LiveViewModel } from './models/live-view.model';
import { SampleDataModel } from './models/sample-data.model';

export const executionTriggered = createAction(
  '[LiveView] Sample Data Parsing Triggered',
  props<{ sampleData: SampleDataModel, chainConfig: {} }>()
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
  executionTriggered,
  liveViewRefreshedSuccessfully,
  liveViewRefreshFailed,
});

export type LiveViewActionsType = typeof actions;
