import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';

import { ChainDetailsModel } from './chain-details.model';
import * as fromActions from './chain-page.actions';
import { ChainPageState, getChain } from './chain-page.reducers';

@Component({
  selector: 'app-chain-page',
  templateUrl: './chain-page.component.html',
  styleUrls: ['./chain-page.component.scss']
})
export class ChainPageComponent implements OnInit {

  details: ChainDetailsModel;
  breadcrumbs = [];
  chainId: string;

  constructor(
    private store: Store<ChainPageState>,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {

    this.activatedRoute.params.subscribe((params) => {
      this.chainId = params.id;
      this.store.dispatch(new fromActions.LoadChainDetailsAction({
        id: params.id
      }));
    });

    this.store.pipe(select(getChain, { id: this.chainId })).subscribe((chain) => {
      if (chain.parsers && chain.parsers.length > 0) {
        this.details = chain;

        this.breadcrumbs = this.breadcrumbs.length > 0 ? this.breadcrumbs : [this.details];

        const chainIndexInPath = this.breadcrumbs.length > 0
          ? this.breadcrumbs.findIndex(breadcrumb => chain.id === breadcrumb.id)
          : null;

        if (chainIndexInPath > -1) {
          this.breadcrumbs[chainIndexInPath] = chain;
        }
      }
    });
  }

  removeParser(id: string) {
    this.store.dispatch(new fromActions.RemoveParserAction({
      id,
      chainId: this.breadcrumbs.length > 0 ? this.breadcrumbs[this.breadcrumbs.length - 1].id : this.chainId
    }));
  }

  onChainLevelChange(chainId: string) {
    this.store.pipe(select(getChain, { id: chainId })).subscribe((chain) => {
      this.breadcrumbs.push(chain);
    });
  }

  onBreadcrumbClick(event: Event, chain: ChainDetailsModel) {
    event.preventDefault();
    const index = this.breadcrumbs.findIndex((ch) => ch.id === chain.id);
    this.breadcrumbs = this.breadcrumbs.slice(0, index + 1);
  }
}
