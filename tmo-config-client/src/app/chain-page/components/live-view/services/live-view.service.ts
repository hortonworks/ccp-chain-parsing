import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { LiveViewModel } from '../models/live-view.model';
import { SampleDataModel } from '../models/sample-data.model';

@Injectable({
  providedIn: 'root'
})
export class LiveViewService {

  readonly SAMPLE_PARSER_URL = '/api/v1/parserconfig/sampleparser/parsingjobs';

  constructor(
    private http: HttpClient,
  ) { }

  execute(sampleData: SampleDataModel, chainConfig: {}): Observable<LiveViewModel> {
    return this.http.post<LiveViewModel>(
      this.SAMPLE_PARSER_URL,
      { sampleData, chainConfig } as LiveViewModel);
  }
}
