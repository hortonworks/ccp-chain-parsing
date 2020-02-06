import {
  executionTriggered,
  liveViewRefreshedSuccessfully,
  liveViewRefreshFailed,
} from './live-view.actions';
import { LiveViewActionsType } from './live-view.actions';
import { initialState, reducer } from './live-view.reducers';
import { SampleDataType } from './models/sample-data.model';

describe('live-view.reducers', () => {

  const testConfigState = {
    id: '123',
    name: 'abcd',
    parsers: []
  };

  const testLiveViewState = {
    sampleData: {
      type: SampleDataType.MANUAL,
      source: '',
    },
    isExecuting: false,
    result: {
      entries: []
    },
  };

  it('should handle default case', () => {
    expect(reducer(undefined, { type: undefined } as LiveViewActionsType)).toBe(initialState);
  });

  it('should update isExecuting on executionTriggered action', () => {
    const newState = reducer(initialState, executionTriggered({ sampleData: testLiveViewState.sampleData, chainConfig: testConfigState }));
    expect(newState.isExecuting).toBe(true);
  });

  it('should update sampleData on executionTriggered action', () => {
    const newState = reducer(initialState, executionTriggered({ sampleData: testLiveViewState.sampleData, chainConfig: testConfigState }));
    expect(newState.isExecuting).toBe(true);
  });

  it('should update isExecuting on liveViewRefreshedSuccessfully action', () => {
    const newState = reducer(initialState, liveViewRefreshedSuccessfully({ liveViewResult: {
      ...testLiveViewState,
      chainConfig: testConfigState,
    } }));
    expect(newState.isExecuting).toBe(false);
  });

  it('should update result on liveViewRefreshedSuccessfully action', () => {
    const result = {
      entries: [
        {
          input: 'input result',
          output: 'output result',
          log: { type: '', message: 'log result'},
        }
      ]
    };

    const newState = reducer(initialState, liveViewRefreshedSuccessfully({ liveViewResult: {
      ...testLiveViewState,
      chainConfig: testConfigState,
      result,
    } }));

    expect(newState.result).toEqual(result);
  });

  it('should update isExecuting on liveViewRefreshFailed action', () => {
    const newState = reducer(initialState, liveViewRefreshFailed({ error: { message: 'ups' } }));
    expect(newState.isExecuting).toBe(false);
  });

});
