
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
      routes: {},
      error: ''
    });
  });
});
