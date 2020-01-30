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

  execute(sampleData: SampleDataModel, chainConfig: ChainDetailsModel): Observable<any> {
    return this.http.post(
      '/api/v1/parserconfig/sampleparser/parsingjobs',
      new LiveViewModel(sampleData, chainConfig))
      .pipe(delay(3000));
  }
}
