import { SampleDataModel } from './sample-data.model';

export interface LiveViewResultModel {
  results: EntryParsingResultModel[];
}

export interface EntryParsingResultModel {
  input: string;
  output: {};
  log: {
    type: string;
    message: string;
    parserId?: string;
  };
}

export interface LiveViewModel {
  sampleData: SampleDataModel;
  chainConfig: {};
  results?: EntryParsingResultModel[];
}
