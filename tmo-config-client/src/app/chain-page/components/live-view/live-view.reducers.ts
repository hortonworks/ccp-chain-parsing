import { LiveViewActionsType, sampleDataChanged, chainConfigChanged, executionTriggered, liveViewRefreshedSuccessfully, liveViewRefreshFailed } from './live-view.actions';
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
    case chainConfigChanged.type: {
      return {
        ...state,
        isModelDirty: true,
      };
    }
    case executionTriggered.type: {
      return {
        ...state,
        isExecuting: true,
      };
    }
    case liveViewRefreshedSuccessfully.type: {
      return {
        ...state,
        isExecuting: false,
        isModelDirty: false,
      };
    }
    case liveViewRefreshFailed.type: {
      return {
        ...state,
        isExecuting: false,
        isModelDirty: true,
      };
    }
    default: {
      return state;
    }
  }
}

// TODO dirty on parser config chain
// TODO trigger action on debounce timer to set isexecute
// TODO spinner on execute
// TODO toggle for on / off Separated ticket!
