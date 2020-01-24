import { sampleDataChanged } from './live-view.actions';
import { LiveViewActionsType } from './live-view.actions';
import { initialState, reducer } from './live-view.reducers';
import { SampleDataModel } from './models/sample-data.model';

describe('live-view.reducers', () => {

  it('should handle default case', () => {
    expect(reducer(undefined, { type: undefined } as LiveViewActionsType)).toBe(initialState);
  });

  it('should update sample data on sampleDataChanged action', () => {
    const sampleData = new SampleDataModel();
    sampleData.source = 'test input';

    const newState = reducer(initialState, sampleDataChanged({ sampleData }));
    expect(newState.model.sampleData.source).toBe('test input');
  });

});
