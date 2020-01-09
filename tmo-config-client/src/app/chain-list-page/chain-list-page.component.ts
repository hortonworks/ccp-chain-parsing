import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { ChainListPageState } from './chain-list-page.reducers'

@Component({
  selector: 'app-chain-list-page',
  templateUrl: './chain-list-page.component.html',
  styleUrls: ['./chain-list-page.component.scss']
})
export class ChainListPageComponent implements OnInit {
  isChainModalVisible = false;
  isOkLoading = false;

  constructor(private store: Store<ChainListPageState>) { }

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
