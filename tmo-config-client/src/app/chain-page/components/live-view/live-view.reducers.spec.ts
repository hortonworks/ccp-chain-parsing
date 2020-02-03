import { sampleDataChanged, chainConfigChanged, executionTriggered, liveViewRefreshedSuccessfully, liveViewRefreshFailed } from './live-view.actions';
import { LiveViewActionsType } from './live-view.actions';
import { initialState, reducer } from './live-view.reducers';
import { SampleDataModel } from './models/sample-data.model';

fdescribe('live-view.reducers', () => {

  const testConfigState = {
    id: '123',
    name: 'abcd',
    parsers: []
  };

  const testLiveViewState = { 
    sampleData: new SampleDataModel(), 
    parserChainConfig: testConfigState,
    result: {
      entries: []
    },
  }

  it('should handle default case', () => {
    expect(reducer(undefined, { type: undefined } as LiveViewActionsType)).toBe(initialState);
  });

  it('should update sample data on sampleDataChanged action', () => {
    const sampleData = new SampleDataModel();
    sampleData.source = 'test input';

    const newState = reducer(initialState, sampleDataChanged({ sampleData }));
    expect(newState.liveViewModel.sampleData.source).toBe('test input');
  });

  it('should update isModelDirty on chainConfigChanged action', () => {
    const newState = reducer(initialState, chainConfigChanged({ chainConfig: testConfigState }));
    expect(newState.isModelDirty).toBe(true);
  });

  it('should update isExecuting on executionTriggered action', () => {
    const newState = reducer(initialState, executionTriggered());
    expect(newState.isExecuting).toBe(true);
  });

  it('should update isExecuting on liveViewRefreshedSuccessfully action', () => {
    const newState = reducer(initialState, liveViewRefreshedSuccessfully({ liveViewResult: testLiveViewState }));
    expect(newState.isExecuting).toBe(false);
  });

  it('should update isModelDirty on liveViewRefreshedSuccessfully action', () => {
    const newState = reducer(initialState, liveViewRefreshedSuccessfully({ liveViewResult: testLiveViewState }));
    expect(newState.isModelDirty).toBe(false);
  });

  it('should update liveViewModel on liveViewRefreshedSuccessfully action', () => {
    const newState = reducer(initialState, liveViewRefreshedSuccessfully({ liveViewResult: testLiveViewState }));
    expect(newState.liveViewModel).toBe(testLiveViewState);
  });

  it('should update isExecuting on liveViewRefreshFailed action', () => {
    const newState = reducer(initialState, liveViewRefreshFailed({ error: { message: 'ups' } }));
    expect(newState.isExecuting).toBe(false);
  });

  it('should update isModelDirty on liveViewRefreshFailed action', () => {
    const newState = reducer(initialState, liveViewRefreshFailed({ error: { message: 'ups' } }));
    expect(newState.isModelDirty).toBe(true);
  });

});
