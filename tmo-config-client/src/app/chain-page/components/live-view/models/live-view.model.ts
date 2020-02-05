import { SampleDataModel } from './sample-data.model';

export interface LiveViewResultModel {
  entries: EntryParsingResultModel[];
}

export interface EntryParsingResultModel {
  input: string;
  output: {};
  log: { type: string, message: string };
}

export interface LiveViewModel {
  sampleData: SampleDataModel;
  chainConfig: {};
  result?: LiveViewResultModel;
}
