import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ChainDetailsModel } from 'src/app/chain-page/chain-details.model';

import { LiveViewModel } from '../models/live-view.model';
import { SampleDataModel } from '../models/sample-data.model';

@Injectable({
  providedIn: 'root'
})
export class LiveViewService {

  constructor(
    private http: HttpClient,
  ) { }

  execute(sampleData: SampleDataModel, chainConfig: ChainDetailsModel): Observable<any> {
    return this.http.post(
      '/api/v1/parserconfig/sampleparser/parsingjobs',
      new LiveViewModel(sampleData, chainConfig))
      .pipe(delay(3000));
  }
}
