import { Component } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { ChainDetailsModel } from '../../chain-details.model';
import { getChainDetails } from '../../chain-page.reducers';

import { chainConfigChanged } from './live-view.actions';
import { LiveViewState } from './live-view.reducers';

@Component({
  selector: 'app-live-view',
  templateUrl: './live-view.component.html',
  styleUrls: ['./live-view.component.scss']
})
export class LiveViewComponent {

  constructor(private store: Store<LiveViewState>) {
    this.store.pipe(select(getChainDetails)).subscribe((chainConfig: ChainDetailsModel) => {
      this.store.dispatch( chainConfigChanged({ chainConfig }));
    });
  }

}
