
// @TODO uncomment

// import * as fromActions from './chain-page.actions';

// import * as fromReducers from './chain-page.reducers';

// describe('chain-page: reducers', () => {

//   it('should remove a selected parser from the store', () => {
//     const state: fromReducers.ChainPageState = {
//       details: {
//         id: '4533',
//         name: 'test chain a',
//         parsers: [
//           {
//             id: '123',
//             name: 'Syslog',
//             type: 'Grok',
//             config: {},
//             input: '',
//             outputs: ''
//           },
//           {
//             id: '456',
//             name: 'Asa',
//             type: 'Grok',
//             config: {},
//             input: '',
//             outputs: ''
//           }
//         ]
//       },
//       error: ''
//     };
//     expect(
//       fromReducers.reducer(state, new fromActions.RemoveParserAction({
//         id: '456',
//         chainId: '4533'
//       }))
//     ).toEqual({
//       ...state,
//       details: {
//         ...state.details,
//         parsers: [{
//           id: '123',
//           name: 'Syslog',
//           type: 'Grok',
//           config: {},
//           input: '',
//           outputs: ''
//         }]
//       }
//     });
//   });
// });
