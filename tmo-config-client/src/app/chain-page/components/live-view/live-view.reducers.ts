import {
  executionTriggered,
  LiveViewActionsType,
  liveViewRefreshedSuccessfully,
  liveViewRefreshFailed,
} from './live-view.actions';
import { LiveViewResultModel } from './models/live-view.model';
import { SampleDataModel, SampleDataType } from './models/sample-data.model';

export interface LiveViewState {
  isExecuting: boolean;
  sampleData: SampleDataModel;
  result: LiveViewResultModel;
}

export const initialState: LiveViewState = {
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
    default: {
      return state;
    }
  }
}
