import { ChainDetailsModel } from '../../../chain-details.model';

import { SampleDataModel } from './sample-data.model';

export class LiveViewResultModel {
  entries: EntryParsingResultModel[];
}

export class EntryParsingResultModel {
  input: string;
  output: {};
  log: { type: string, message: string };
}

export class LiveViewModel {

  sampleData: SampleDataModel;
  parserChainConfig: ChainDetailsModel;
  result: LiveViewResultModel;

  constructor(sampleData = new SampleDataModel(), chainConfig?: ChainDetailsModel) {
    this.sampleData = sampleData;
    this.parserChainConfig = chainConfig;
  }

}
