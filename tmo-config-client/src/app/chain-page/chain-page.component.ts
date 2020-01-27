import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';

import { ChainDetailsModel } from './chain-details.model';
import * as fromActions from './chain-page.actions';
import { ChainPageState, getChainDetails } from './chain-page.reducers';

@Component({
  selector: 'app-chain-page',
  templateUrl: './chain-page.component.html',
  styleUrls: ['./chain-page.component.scss']
})
export class ChainPageComponent implements OnInit {

  details: ChainDetailsModel;
  breadcrumbs = [];

  constructor(
    private store: Store<ChainPageState>,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {

    this.activatedRoute.params.subscribe((params) => {
      this.store.dispatch(new fromActions.LoadChainDetailsAction({
        id: params.id
      }));
    });

    this.store.pipe(select(getChainDetails)).subscribe((details) => {
      this.details = details;
      this.breadcrumbs = [this.details];
    });
  }

  removeParser(id: string) {
    this.store.dispatch(new fromActions.RemoveParserAction({ id, chainId: this.details.id }));
  }

  onChainLevelChange(chain: ChainDetailsModel) {
    this.breadcrumbs.push(chain);
  }

  onBreadcrumbClick(event: Event, chain: ChainDetailsModel) {
    event.preventDefault();
    const index = this.breadcrumbs.indexOf(chain);
    this.breadcrumbs = this.breadcrumbs.slice(0, index + 1);
  }

}
