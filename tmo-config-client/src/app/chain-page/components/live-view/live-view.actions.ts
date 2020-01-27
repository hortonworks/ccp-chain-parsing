import { createAction, props, union } from '@ngrx/store';

import { SampleDataModel } from './models/sample-data.model';

export const sampleDataChanged = createAction(
  '[LiveView] Sample Data Changed',
  props<{ sampleData: SampleDataModel }>()
);

const actions = union({ sampleDataChanged });

export type LiveViewActionsType = typeof actions;
