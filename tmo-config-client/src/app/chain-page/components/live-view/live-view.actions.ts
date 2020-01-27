import { createAction, props, union } from '@ngrx/store';

import { ChainDetailsModel } from '../../chain-details.model';

import { SampleDataModel } from './models/sample-data.model';

export const sampleDataChanged = createAction(
  '[LiveView] Sample Data Changed',
  props<{ sampleData: SampleDataModel }>()
);

export const chainConfigChanged = createAction(
  '[LiveView] Parser Chain Configuration Changed',
  props<{ chainConfig: ChainDetailsModel }>()
);

export const refreshTick = createAction(
  '[LiveView] Refresh Clock Ticked',
  props<{ chainConfig: ChainDetailsModel }>()
);

const actions = union({
  sampleDataChanged,
  chainConfigChanged,
  refreshTick
});

export type LiveViewActionsType = typeof actions;
