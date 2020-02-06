import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';

import { ChainDetailsModel } from './chain-details.model';
import * as fromActions from './chain-page.actions';
import { ChainPageState, getChainDetails } from './chain-page.reducers';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-chain-page',
  templateUrl: './chain-page.component.html',
  styleUrls: ['./chain-page.component.scss']
})
export class ChainPageComponent implements OnInit {

  details: ChainDetailsModel;
  chainConfig$: Observable<ChainDetailsModel>;

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

    this.chainConfig$ = this.store.pipe(select(getChainDetails));
    this.chainConfig$.subscribe((details) => {
      this.details = details;
    });
  }

  removeParser(id: string) {
    this.store.dispatch(new fromActions.RemoveParserAction({ id, chainId: this.details.id }));
  }

}
