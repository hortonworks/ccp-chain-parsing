
import * as fromActions from './chain-page.actions';

import * as fromReducers from './chain-page.reducers';

describe('chain-page: reducers', () => {

  it('should remove a selected parser from the store', () => {
    const state: fromReducers.ChainPageState = {
      chains: {
        4533: {
          id: '4533',
          name: 'test chain a',
          parsers: ['123', '456']
        }
      },
      parsers: {
        123: {
          id: '123',
          name: 'Syslog',
          type: 'Grok',
          config: {},
          input: '',
          outputs: ''
        },
        456: {
          id: '456',
          name: 'Asa',
          type: 'Grok',
          config: {},
          input: '',
          outputs: ''
        }
      },
      dirtyParsers: [],
      dirtyChains: [],
      routes: {},
      error: ''
    };
    expect(
      fromReducers.reducer(state, new fromActions.RemoveParserAction({
        id: '456',
        chainId: '4533'
      }))
    ).toEqual({
      chains: {
        4533: {
          id: '4533',
          name: 'test chain a',
          parsers: ['123']
        }
      },
      parsers: {
        123: {
          id: '123',
          name: 'Syslog',
          type: 'Grok',
          config: {},
          input: '',
          outputs: ''
        }
      },
      routes: {},
      dirtyParsers: [],
      dirtyChains: ['4533'],
      error: ''
    });
  });

  it('load success: should set all the components', () => {
    const state = {
      chains: null,
      parsers: null,
      routes: null,
      dirtyParsers: [],
      dirtyChains: [],
      error: ''
    };
    const chains = {};
    const parsers = {};
    const routes = {};
    const newState = fromReducers.reducer(state, new fromActions.LoadChainDetailsSuccessAction({
      chains,
      parsers,
      routes
    }));
    expect(newState.chains).toBe(chains);
    expect(newState.parsers).toBe(parsers);
    expect(newState.routes).toBe(routes);
  });

  it('should update the given parser', () => {
    const state = {
      chains: null,
      parsers: {
        456: {
          id: '456',
          type: 'grok',
          name: 'some parser',
          outputs: 'old',
          config: 'old'
        }
      },
      routes: null,
      dirtyParsers: [],
      dirtyChains: [],
      error: ''
    };
    const newState = fromReducers.reducer(state, new fromActions.UpdateParserAction({
      chainId: '123',
      parser: {
        id: '456',
        outputs: 'new',
        config: 'new'
      }
    }));
    expect(newState.parsers['456']).toEqual({
      id: '456',
      type: 'grok',
      name: 'some parser',
      outputs: 'new',
      config: 'new'
    });
    expect(newState.dirtyParsers).toEqual(['456']);
    expect(newState.dirtyChains).toEqual(['123']);
  });

  it('should update the given chain name', () => {
    const state = {
      parsers: null,
      chains: {
        456: {
          id: '456',
          name: 'old',
          parsers: []
        }
      },
      routes: null,
      dirtyParsers: [],
      dirtyChains: [],
      error: ''
    };
    const newState = fromReducers.reducer(state, new fromActions.UpdateChainAction({
      chain: {
        id: '456',
        name: 'new',
        parsers: []
      }
    }));
    expect(newState.chains['456']).toEqual({
      id: '456',
      name: 'new',
      parsers: []
    });
    expect(newState.dirtyChains).toEqual(['456']);
  });

  it('should return with the desired parser', () => {
    const desiredParser = {
      id: '456',
      type: 'grok',
      name: 'some parser',
      outputs: 'old',
      config: 'old'
    };
    const state = {
      'chain-page': {
        chains: null,
        parsers: {
          456: desiredParser
        },
        routes: null,
        error: ''
      }
    };
    const parser = fromReducers.getParser(state, { id: '456' });
    expect(parser).toBe(desiredParser);
  });

  it('should return with the desired chain', () => {
    const desiredChain = {
      id: '456',
      name: 'some chain',
      parsers: []
    };
    const state = {
      'chain-page': {
        chains: {
          456: desiredChain
        },
        parsers: null,
        routes: null,
        error: ''
      }
    };
    const chain = fromReducers.getChain(state, { id: '456' });
    expect(chain).toBe(desiredChain);
  });

  it('should return with the desired route', () => {
    const desiredRoute = {
      id: '456',
      name: 'some route',
      subchain: ''
    };
    const state = {
      'chain-page': {
        chains: null,
        parsers: null,
        routes: {
          456: desiredRoute
        },
        error: ''
      }
    };
    const route = fromReducers.getRoute(state, { id: '456' });
    expect(route).toBe(desiredRoute);
  });
});
