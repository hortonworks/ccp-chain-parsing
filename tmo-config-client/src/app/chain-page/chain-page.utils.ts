// const chain = wrapper.details ? wrapper.details : wrapper.subchain ? wrapper.subchain : null;
// const parsers = chain && chain.parsers ? [...chain.parsers] : [];
// for (let i = 0; i < parsers.length; i++) {
//   if (parsers[i].id === newParser.id) {
//     parsers[i] = {
//       ...parsers[i],
//       newParser
//     };
//     const indexInBreadcrumbs = this.breadcrumbs.findIndex(ch => ch.id === chain.id);
//     if (indexInBreadcrumbs > -1) {
//       this.breadcrumbs[indexInBreadcrumbs].parsers = parsers;
//     }
//   } else if (parsers[i].config && parsers[i].config.routes) {
//     for (const route of parsers[i].config.routes) {
//       this.findAndUpdateParser(route, newParser);
//     }
//   }
// }



export function normalizeParserConfig(config, normalized?) {
  normalized = normalized || {
    parsers: {},
    chains: {},
    routes: {}
  };
  if (config.parsers) {
    normalized.chains[config.id] = {
      ...config
    };
    normalized.chains[config.id].parsers = config.parsers.map((parser) => {
      normalized.parsers[parser.id] = { ...parser };
      if (parser.type === 'Router' && parser.config && parser.config.routes) {
        normalized.parsers[parser.id].config = {
          ...parser.config,
          routes: parser.config.routes.map((route) => {
            normalized.routes[route.id] = { ...route };
            if (route.subchain) {
              const chainId = route.subchain.id;
              normalizeParserConfig(route.subchain, normalized);
              normalized.routes[route.id].subchain = chainId;
            }
            return route.id;
          })
        };
      }
      return parser.id;
    });
  }
  return normalized;
}

export function denormalizeParserConfig(chain, config) {
  const denormalized = {
    ...chain
  };
  if (denormalized.parsers) {
    denormalized.parsers = chain.parsers.map((parserId) => {
      const parser = config.parsers[parserId];
      if (parser.type === 'Router') {
        parser.config = {
          ...parser.config,
          routes: parser.config.routes.map((routeId) => {
            const route = config.routes[routeId];
            const subchainId = route.subchain;
            if (subchainId) {
              const subchain = config.chains[subchainId];
              route.subchain = denormalizeParserConfig(subchain, config);
            }
            return route;
          })
        };
      }
      return parser;
    });
  }
  return denormalized;
}
