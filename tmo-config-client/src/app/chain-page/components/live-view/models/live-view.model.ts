import { ChainDetailsModel } from '../../../chain-page.models';

import { SampleDataModel } from './sample-data.model';

export class LiveViewResultModel {
  entries: EntryParsingResultModel[];
}

export class EntryParsingResultModel {
  input: string;
  output: string;
  log: { type: string, message: string };
}

export class LiveViewModel {
  sampleData = new SampleDataModel();
  chainConfig: ChainDetailsModel;
  parsingResult: LiveViewResultModel;
}
