export interface ParserModel {
  id: string;
  name: string;
  chainID?: string;
  type: string;
  config: any;
  input: any;
  outputs: any;
}

export interface ChainDetailsModel {
  id: string;
  name: string;
  parsers: ParserModel[];
}
