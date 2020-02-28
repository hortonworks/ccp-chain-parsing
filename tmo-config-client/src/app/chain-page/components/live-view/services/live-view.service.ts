import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { EntryParsingResultModel, LiveViewModel } from '../models/live-view.model';
import { SampleDataModel } from '../models/sample-data.model';

@Injectable({
  providedIn: 'root'
})
export class LiveViewService {

  readonly SAMPLE_PARSER_URL = '/api/v1/parserconfig/tests';

  constructor(
    private http: HttpClient,
  ) { }

  execute(sampleData: SampleDataModel, chainConfig: {}): Observable<{ results: EntryParsingResultModel[]}> {
    return this.http.post<{ results: EntryParsingResultModel[]}>(
      this.SAMPLE_PARSER_URL,
      { sampleData, chainConfig } as LiveViewModel);
  }
}
