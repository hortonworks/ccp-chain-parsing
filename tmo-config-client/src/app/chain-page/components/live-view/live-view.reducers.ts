import { LiveViewActionsType, sampleDataChanged } from './live-view.actions';
import { LiveViewModel } from './models/live-view.model';
import { SampleDataModel } from './models/sample-data.model';

export interface LiveViewState {
  isExecuting: boolean;
  isLiveViewOn: boolean;
  error: string;
  isModelDirty: boolean;
  liveViewModel: LiveViewModel;
}

export const initialState: LiveViewState = {
  isExecuting: false,
  isLiveViewOn: true,
  error: '',
  isModelDirty: false,
  liveViewModel: new LiveViewModel(),
};

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

      const newSampleData = new SampleDataModel();
      newSampleData.type = action.sampleData.type;
      newSampleData.source = action.sampleData.source;

      const newLiveView = new LiveViewModel();
      newLiveView.sampleData = newSampleData;

      newState.liveViewModel = newLiveView;
      return newState;
    }
    default: {
      return state;
    }
  }
}
