import { createSelector } from '@ngrx/store';

import * as addParserActions from '../chain-add-parser-page/chain-add-parser-page.actions';
import { ChainModel } from '../chain-list-page/chain.model';

import * as chainPageActions from './chain-page.actions';
import { ParserChainModel, ParserModel, RouteModel } from './chain-page.models';
import { denormalizeParserConfig } from './chain-page.utils';
import { CustomFormConfig } from './components/custom-form/custom-form.component';

export interface ChainPageState {
  chains: { [key: string]: ParserChainModel };
  parsers: { [key: string]: ParserModel };
  routes: { [key: string]: RouteModel };
  error: string;
  formConfigs?: { [key: string]: CustomFormConfig[] };
  dirtyParsers: string[];
  dirtyChains: string[];
  path: string[];
}

export const initialState: ChainPageState = {
  chains: {},
  parsers: {},
  routes: {},
  error: '',
  formConfigs: {},
  dirtyParsers: [],
  dirtyChains: [],
  path: [],
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
        routes: action.payload.routes,
        dirtyParsers: [],
        dirtyChains: [],
        path: [action.payload.chainId]
      };
    }
    case chainPageActions.REMOVE_PARSER: {
      const parsers = { ...state.parsers };
      delete parsers[action.payload.id];
      return {
        ...state,
        parsers,
        dirtyChains: [
          ...state.dirtyChains.filter(id => id !== action.payload.chainId),
          action.payload.chainId
        ],
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
        },
        dirtyChains: [
          ...state.dirtyChains.filter(id => id !== action.payload.chainId),
          action.payload.chainId
        ],
        dirtyParsers: [
          ...state.dirtyParsers.filter(parserId => parserId !== action.payload.parser.id),
          action.payload.parser.id
        ],
      };
    }
    case chainPageActions.ADD_CHAIN: {
      return {
        ...state,
        chains: {
          ...state.chains,
          [action.payload.chain.id]: {
            ...action.payload.chain as ChainModel,
            parsers: []
          }
        }
      };
    }
    case chainPageActions.UPDATE_CHAIN: {
      return {
        ...state,
        dirtyChains: [
          ...state.dirtyChains.filter(id => id !== action.payload.chain.id),
          action.payload.chain.id
        ],
        chains: {
          ...state.chains,
          [action.payload.chain.id]: {
            ...state.chains[action.payload.chain.id],
            ...action.payload.chain
          }
        }
      };
    }
    case addParserActions.ADD_PARSER: {
      return {
        ...state,
        parsers: {
          ...state.parsers,
          [action.payload.parser.id]: action.payload.parser
        },
        dirtyParsers: [
          ...state.dirtyParsers.filter(parserId => parserId !== action.payload.parser.id),
          action.payload.parser.id
        ],
        dirtyChains: [
          ...state.dirtyChains.filter(id => id !== action.payload.chainId),
          action.payload.chainId
        ],
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
    case chainPageActions.SAVE_PARSER_CONFIG: {
      return {
        ...state,
        dirtyChains: [],
        dirtyParsers: []
      };
    }
    case chainPageActions.ADD_ROUTE: {
      return {
        ...state,
        dirtyParsers: [
          ...state.dirtyParsers.filter(parserId => parserId !== action.payload.parserId),
          action.payload.parserId
        ],
        dirtyChains: [
          ...state.dirtyChains.filter(id => id !== action.payload.chainId),
          action.payload.chainId
        ],
        parsers: {
          ...state.parsers,
          [action.payload.parserId]: {
            ...state.parsers[action.payload.parserId],
            config: {
              ...(state.parsers[action.payload.parserId].config || {}),
              routes: [
                ...((state.parsers[action.payload.parserId].config || {}).routes || []).filter(id => id !== action.payload.route.id),
                action.payload.route.id
              ]
            }
          }
        },
        routes: {
          ...state.routes,
          [action.payload.route.id]: {
            ...action.payload.route as RouteModel
          }
        }
      };
    }
    case chainPageActions.UPDATE_ROUTE: {
      return {
        ...state,
        dirtyParsers: [
          ...state.dirtyParsers.filter(parserId => parserId !== action.payload.parserId),
          action.payload.parserId
        ],
        dirtyChains: [
          ...state.dirtyChains.filter(id => id !== action.payload.chainId),
          action.payload.chainId
        ],
        routes: {
          ...state.routes,
          [action.payload.route.id]: {
            ...state.routes[action.payload.route.id],
            ...action.payload.route
          }
        }
      };
    }
    case chainPageActions.REMOVE_ROUTE: {
      const routes = { ...state.routes };
      delete routes[action.payload.routeId];
      return {
        ...state,
        dirtyParsers: [
          ...state.dirtyParsers.filter(parserId => parserId !== action.payload.parserId),
          action.payload.parserId
        ],
        dirtyChains: [
          ...state.dirtyChains.filter(id => id !== action.payload.chainId),
          action.payload.chainId
        ],
        routes,
        parsers: {
          ...state.parsers,
          [action.payload.parserId]: {
            ...state.parsers[action.payload.parserId],
            config: {
              ...state.parsers[action.payload.parserId].config,
              routes: state.parsers[action.payload.parserId].config.routes.filter(id => id !== action.payload.parserId)
            }
          }
        }
      };
    }
    case chainPageActions.ADD_TO_PATH: {
      return {
        ...state,
        path: [
          ...state.path.filter(chainId => chainId !== action.payload.chainId),
          action.payload.chainId
        ]
      };
    }
    case chainPageActions.REMOVE_FROM_PATH: {
      return {
        ...state,
        path: state.path.filter(chainId => !action.payload.chainId.includes(chainId))
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

export const getDirtyParsers = createSelector(
  getChainPageState,
  (state) => state.dirtyParsers
);

export const getDirtyChains = createSelector(
  getChainPageState,
  (state) => state.dirtyChains
);

export const getDirtyStatus = createSelector(
  getDirtyParsers,
  getDirtyChains,
  (parsers: string[], chains: string[]) => ({
    dirtyChains: chains,
    dirtyParsers: parsers
  })
);

export const getFormConfigByType = createSelector(
  getChainPageState,
  (state, props) => (state.formConfigs || {})[props.type]
);

export const getFormConfigs = createSelector(
  getChainPageState,
  (state) => state.formConfigs
);

export const getPath = createSelector(
  getChainPageState,
  (state) => state.path
);

export const getPathWithChains = createSelector(
  getPath,
  getChains,
  (path, chains) => path.map(chainId => chains[chainId])
);
