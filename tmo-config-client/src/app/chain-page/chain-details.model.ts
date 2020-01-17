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

export interface ChainDetailsModel {
  id: string;
  name: string;
  parsers: ParserModel[];
}
