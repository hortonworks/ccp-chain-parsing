import { createSelector } from '@ngrx/store';

import * as addParserActions from '../chain-add-parser-page/chain-add-parser-page.actions';

import * as chainPageActions from './chain-page.actions';
import { ParserChainModel, ParserModel, RouteModel } from './chain-page.models';
import { denormalizeParserConfig } from './chain-page.utils';
import { CustomFormConfig } from './components/custom-form/custom-form.component';

export interface ChainPageState {
  chains: { [key: string]: ParserChainModel };
  parsers: { [key: string]: ParserModel };
  routes: { [key: string]: RouteModel };
  dirty: boolean;
  error: string;
  formConfigs?: { [key: string]: CustomFormConfig[] };
}

export const initialState: ChainPageState = {
  chains: {},
  parsers: {},
  routes: {},
  dirty: false,
  error: '',
  formConfigs: {}
};

export function reducer(
  state: ChainPageState = initialState,
  action: chainPageActions.ChainDetailsAction | addParserActions.ParserAction
): ChainPageState {
  switch (action.type) {
    case chainPageActions.LOAD_CHAIN_DETAILS_SUCCESS: {
      return {
        ...state,
        chains: action.payload.chains,
        parsers: action.payload.parsers,
        routes: action.payload.routes
      };
    }
    case chainPageActions.REMOVE_PARSER: {
      const parsers = { ...state.parsers };
      delete parsers[action.payload.id];
      return {
        ...state,
        parsers,
        chains: {
          ...state.chains,
          [action.payload.chainId]: {
            ...state.chains[action.payload.chainId],
            parsers: (state.chains[action.payload.chainId].parsers as string[])
              .filter((parserId: string) => parserId !== action.payload.id)
          }
        }
      };
    }
    case chainPageActions.UPDATE_PARSER: {
      return {
        ...state,
        parsers: {
          ...state.parsers,
          [action.payload.parser.id]: {
            ...state.parsers[action.payload.parser.id],
            ...action.payload.parser
          }
        }
      };
    }
    case chainPageActions.UPDATE_CHAIN: {
      return {
        ...state,
        chains: {
          ...state.chains,
          [action.payload.chain.id]: {
            ...state.chains[action.payload.chain.id],
            ...action.payload.chain
          }
        }
      };
    }
    case chainPageActions.SET_DIRTY: {
      return {
        ...state,
        dirty: action.payload.dirty
      };
    }
    case addParserActions.ADD_PARSER: {
      return {
        ...state,
        parsers: {
          ...state.parsers,
          [action.payload.parser.id]: action.payload.parser
        },
        chains: {
          ...state.chains,
          [action.payload.chainId]: {
            ...state.chains[action.payload.chainId],
            parsers: [
              ...(state.chains[action.payload.chainId].parsers as string[]),
              action.payload.parser.id
            ]
          }
        }
      };
    }
    case chainPageActions.GET_FORM_CONFIG_SUCCESS: {
      return {
        ...state,
        formConfigs: {
          ...(state.formConfigs || {}),
          [action.payload.parserType]: action.payload.formConfig
        }
      };
    }
    case chainPageActions.GET_FORM_CONFIGS_SUCCESS: {
      return {
        ...state,
        formConfigs: action.payload.formConfigs
      };
    }
  }
  return state;
}

export function getChainPageState(state: any): ChainPageState {
  return state['chain-page'];
}

export const getChains = createSelector(
  getChainPageState,
  (state: ChainPageState): { [key: string]: ParserChainModel } => {
    return state.chains;
  }
);

export const getChain = createSelector(
  getChainPageState,
  (state, props): ParserChainModel => {
    return state.chains[props.id];
  }
);

export const getParser = createSelector(
  getChainPageState,
  (state, props): ParserModel => {
    return state.parsers[props.id];
  }
);

export const getRoute = createSelector(
  getChainPageState,
  (state, props): RouteModel => {
    return state.routes[props.id];
  }
);

export const getChainDetails = createSelector(
  getChainPageState,
  (state, props) => {
    const mainChain = state.chains[props.chainId];
    return denormalizeParserConfig(mainChain, state);
  }
);

export const isDirty = createSelector(
  getChainPageState,
  (state) => state.dirty
);

export const getFormConfigByType = createSelector(
  getChainPageState,
  (state, props) => (state.formConfigs || {})[props.type]
);

export const getFormConfigs = createSelector(
  getChainPageState,
  (state) => state.formConfigs
);
