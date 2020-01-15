import * as fromActions from './chain-add-parser-page.actions';

import * as fromReducers from './chain-add-parser-page.reducers';

describe('chain add parser page: reducers', () => {

  it('should return with the initial state.', () => {
    expect(fromReducers.reducer(undefined, new fromActions.NoopAction())).toBe(fromReducers.initialState);
  });

  it('should set the parser types', () => {
    const state = {} as unknown;
    const parserTypes = [];
    const newState = fromReducers.reducer(
      state as fromReducers.AddParserPageState,
      new fromActions.GetParserTypesSuccessAction(parserTypes)
    );
    expect(newState.parserTypes).toBe(parserTypes);
  });

  it('should set the parsers', () => {
    const state = {} as unknown;
    const parsers = [];
    const newState = fromReducers.reducer(
      state as fromReducers.AddParserPageState,
      new fromActions.GetParsersSuccessAction(parsers)
    );
    expect(newState.parsers).toBe(parsers);
  });
});

describe('chain add parser page: selectors', () => {

  it('should return with the parser types from state', () => {
    const parserTypesFromState = [];
    const parserTypes = fromReducers.getParserTypes({
      'chain-add-parser-page': {
        parserTypes: parserTypesFromState
      }
    });
    expect(parserTypes).toBe(parserTypesFromState);
  });

  it('should return with the parsers from state', () => {
    const parsersFromState = [];
    const parserTypes = fromReducers.getParsers({
      'chain-add-parser-page': {
        parsers: parsersFromState
      }
    });
    expect(parserTypes).toBe(parsersFromState);
  });
});
