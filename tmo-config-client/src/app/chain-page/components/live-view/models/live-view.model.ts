import { SampleDataModel } from './sample-data.model';
import { ChainDetailsModel } from '../../../chain-details.model';

export class LiveViewResultModel {
  entries: EntryParsingResultModel[];
}

export class EntryParsingResultModel {
  input: String;
  output: String;
  log: { type: string, message: string };
}

export class LiveViewModel {
  sampleData = new SampleDataModel();
  chainConfig: ChainDetailsModel;
  parsingResult: LiveViewResultModel;
}