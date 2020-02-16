import {
  executionTriggered,
  LiveViewActionsType,
  liveViewRefreshedSuccessfully,
  liveViewRefreshFailed,
  onOffToggleChanged,
  onOffToggleRestored,
  sampleDataRestored,
  sampleDataInputChanged,
} from './live-view.actions';
import { LiveViewResultModel } from './models/live-view.model';
import { SampleDataModel, SampleDataType } from './models/sample-data.model';

export interface LiveViewState {
  isLiveViewOn: boolean;
  isExecuting: boolean;
  sampleData: SampleDataModel;
  result: LiveViewResultModel;
}

export const initialState: LiveViewState = {
  isLiveViewOn: false,
  isExecuting: false,
  sampleData: {
    type: SampleDataType.MANUAL,
    source: '',
  },
  result: {
    entries: []
  },
};

export function reducer(
  state: LiveViewState = initialState,
  action: LiveViewActionsType
): LiveViewState {
  switch (action.type) {
    case executionTriggered.type: {
      return {
        ...state,
        sampleData: action.sampleData,
        isExecuting: true,
      };
    }
    case liveViewRefreshedSuccessfully.type: {
      return {
        ...state,
        isExecuting: false,
        result: action.liveViewResult.result,
      };
    }
    case liveViewRefreshFailed.type: {
      return {
        ...state,
        isExecuting: false,
      };
    }
    case onOffToggleChanged.type: {
      return {
        ...state,
        isLiveViewOn: action.value,
      };
    }
    case onOffToggleRestored.type: {
      if (action.value === null) return state;

      return {
        ...state,
        isLiveViewOn: action.value
      };
    }
    case sampleDataInputChanged.type: {
      return {
        ...state,
        sampleData: action.sampleData,
      };
    }
    case sampleDataRestored.type: {
      if (action.sampleData === null) return state;

      return {
        ...state,
        sampleData: action.sampleData
      };
    }
    default: {
      return state;
    }
  }
}
