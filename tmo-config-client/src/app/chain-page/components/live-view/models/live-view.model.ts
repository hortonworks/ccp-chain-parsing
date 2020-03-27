import { SampleDataRequestModel } from './sample-data.model';

export interface EntryParsingResultModel {
  input: string;
  output: {};
  log: {
    type: string;
    message: string;
    parserId?: string;
    stackTrace: string;
  };
  parserResults?: ParserResultsModel;
}

export interface ParserResultsModel {
  input: {};
  output: {};
  log: {
    type: string;
    message: string;
    parserId?: string;
    stackTrace: string;
  };
}

export interface LiveViewRequestModel {
  sampleData: SampleDataRequestModel;
  chainConfig: {};
}
