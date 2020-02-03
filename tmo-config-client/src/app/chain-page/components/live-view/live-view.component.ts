import { Component } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { ChainDetailsModel } from '../../chain-details.model';

import { chainConfigChanged } from './live-view.actions';
import { LiveViewState } from './live-view.reducers';
import { getExecutionStatus, getChainConfig } from './live-view.selectors';

@Component({
  selector: 'app-live-view',
  templateUrl: './live-view.component.html',
  styleUrls: ['./live-view.component.scss']
})
export class LiveViewComponent {

  isExecuting$: Observable<boolean>;

  constructor(private store: Store<LiveViewState>) {
    this.store.pipe(select(getChainConfig)).subscribe((chainConfig: ChainDetailsModel) => {
      this.store.dispatch( chainConfigChanged({ chainConfig }));
    });

    this.isExecuting$ = this.store.pipe(select(getExecutionStatus));
  }

}
