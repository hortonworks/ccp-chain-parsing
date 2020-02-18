export interface ParserChainModel {
  id: string;
  name: string;
  parsers: string[] | ParserModel[];
}

export interface PartialParserChainModel {
  id?: string;
  name?: string;
  parsers?: string[] | ParserModel[];
}

export interface RouteModel {
  id: string;
  name: string;
  subchain: string | ParserChainModel;
}

export interface ParserModel {
  id: string;
  type: string;
  name: string;
  parentId?: string;
  chainId?: string;
  config?: any;
  input?: any;
  outputs?: any;
}

export interface PartialParserModel {
  id?: string;
  type?: string;
  name?: string;
  parentId?: string;
  chainId?: string;
  config?: any;
  input?: any;
  outputs?: any;
}

export interface ChainDetailsModel {
  id: string;
  name: string;
  parsers: ParserModel[];
}
