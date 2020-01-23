import { LiveViewActionsType, sampleDataChanged } from './live-view.actions';
import { LiveViewModel } from './models/live-view.model';

export interface LiveViewState {
  isExecuting: boolean;
  isLivewViewOn: boolean;
  error: string;
  isModelDirty: boolean;
  model: LiveViewModel;
}

export const initialState: LiveViewState = {
  isExecuting: false,
  isLivewViewOn: true,
  error: '',
  isModelDirty: false,
  model: new LiveViewModel(),
}

export function reducer(
  state: LiveViewState = initialState,
  action: LiveViewActionsType
): LiveViewState {
  switch (action.type) {
    case sampleDataChanged.type: {
      const newState = {
        ...state,
        isModelDirty: true,
      };
      newState.model.sampleData = action.sampleData;
      return newState;
    }
    default: {
      return state;
    }
  }
}
