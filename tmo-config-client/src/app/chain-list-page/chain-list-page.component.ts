import { Component, OnInit } from '@angular/core';
import { Store, select } from '@ngrx/store';

import { ChainListPageState, getChains } from './chain-list-page.reducers'
import { Observable } from 'rxjs';
import { ChainModel } from './chain.model';
import { LoadChainsAction } from './chain-list-page.actions';

@Component({
  selector: 'app-chain-list-page',
  templateUrl: './chain-list-page.component.html',
  styleUrls: ['./chain-list-page.component.scss']
})
export class ChainListPageComponent implements OnInit {
  isChainModalVisible = false;
  isOkLoading = false;

  chains$: Observable<ChainModel[]>;

  constructor(private store: Store<ChainListPageState>) {
    store.dispatch(new LoadChainsAction());
    this.chains$ = store.pipe(select(getChains));
  }

  showAddChainModal(): void {
    this.isChainModalVisible = true;
  }

  handleOk(): void {
    this.isOkLoading = true;
    setTimeout(() => {
      this.isChainModalVisible = false;
      this.isOkLoading = false;
    }, 1000);
  }

  handleCancel(): void {
    this.isChainModalVisible = false;
  }

  ngOnInit() {
  }

}
