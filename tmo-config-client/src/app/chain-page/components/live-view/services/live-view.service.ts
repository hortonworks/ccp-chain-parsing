import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';

import { LiveViewState } from '../live-view.reducers';
import { ChainDetailsModel } from 'src/app/chain-page/chain-details.model';
import { SampleDataModel } from '../models/sample-data.model';
import { HttpClient } from '@angular/common/http';
import { LiveViewModel } from '../models/live-view.model';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LiveViewService {

  constructor(
    private http: HttpClient,
    private store: Store<LiveViewState>
  ) { }

  execute(sampleData: SampleDataModel, chainConfig: ChainDetailsModel): Observable<LiveViewModel> {
    // return this.http.post('/api/v1/parserconfig/sampleparser/parsingjobs', new LiveViewModel(sampleData, chainConfig));
    const fakeResponse = new LiveViewModel(sampleData, chainConfig);
    fakeResponse.result = {
      entries: [
        {
          input: '<167>Jan  5 08:52:35 10.22.8.216 %ASA-7-609001: Built local-host inside:10.22.8.205',
          output: {
            original_string: 'the same above',
            ASA_TAG: '%ASA-7-609001',
            ASA_message: 'Built local-host inside:10.22.8.205',
            syslogMessage: '%ASA-7-609001: Built local-host inside:10.22.8.205'
          },
          log: {
            type: 'info | warning | error',
            message: 'Parsing Successful'
          }
        },
        {
          input: '<167>Jan  5 08:52:35 10.22.8.216 %ASA------7-6090014523243: Built local-host far-outside:214.04.72.128',
          output: undefined,
          log: {
            type: 'error',
            message: 'Parsing Failed: ASA_TAG not found'
          }
        }
      ]
    };

    return of(fakeResponse).pipe(delay(3000));
  }
}
