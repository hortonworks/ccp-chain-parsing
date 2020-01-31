import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';

import * as fromActions from './chain-page.actions';
import { ParserChainModel } from './chain-page.models';
import { ChainPageState, getChain } from './chain-page.reducers';

@Component({
  selector: 'app-chain-page',
  templateUrl: './chain-page.component.html',
  styleUrls: ['./chain-page.component.scss']
})
export class ChainPageComponent implements OnInit {

  chain: ParserChainModel;
  breadcrumbs: ParserChainModel[] = [];
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

    this.store.pipe(select(getChain, { id: this.chainId })).subscribe((chain: ParserChainModel) => {
      if (chain && chain.parsers && chain.parsers.length > 0) {
        this.chain = chain;

        this.breadcrumbs = this.breadcrumbs.length > 0 ? this.breadcrumbs : [this.chain];

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
    const chainId = this.breadcrumbs.length > 0
      ? this.breadcrumbs[this.breadcrumbs.length - 1].id
      : this.chainId;
    this.store.dispatch(new fromActions.RemoveParserAction({
      id,
      chainId
    }));
  }

  onChainLevelChange(chainId: string) {
    this.store.pipe(select(getChain, { id: chainId })).subscribe((chain: ParserChainModel) => {
      const breadcrumbIndex = this.breadcrumbs.findIndex((breadcrumb) => breadcrumb.id === chain.id);
      if (breadcrumbIndex > -1) {
        this.breadcrumbs[breadcrumbIndex] = chain;
      } else {
        this.breadcrumbs.push(chain);
      }
    });
  }

  onBreadcrumbClick(event: Event, chain: ParserChainModel) {
    event.preventDefault();
    const index = this.breadcrumbs.findIndex((breadcrumb: ParserChainModel) => breadcrumb.id === chain.id);
    this.breadcrumbs = this.breadcrumbs.slice(0, index + 1);
  }
}
