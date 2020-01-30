import { Component } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { ChainDetailsModel } from '../../chain-details.model';
import { getChainDetails } from '../../chain-page.reducers';

import { chainConfigChanged } from './live-view.actions';
import { LiveViewState } from './live-view.reducers';
import { getExecutionStatus, getResults } from './live-view.selectors';
import { LiveViewResultModel } from './models/live-view.model';

@Component({
  selector: 'app-live-view',
  templateUrl: './live-view.component.html',
  styleUrls: ['./live-view.component.scss']
})
export class LiveViewComponent {

  isExecuting$: Observable<boolean>;
  results$: Observable<LiveViewResultModel>;

  constructor(private store: Store<LiveViewState>) {
    this.store.pipe(select(getChainDetails)).subscribe((chainConfig: ChainDetailsModel) => {
      this.store.dispatch( chainConfigChanged({ chainConfig }));
    });

    this.isExecuting$ = this.store.pipe(select(getExecutionStatus));
    this.results$ = this.store.pipe(select(getResults));
  }

}
