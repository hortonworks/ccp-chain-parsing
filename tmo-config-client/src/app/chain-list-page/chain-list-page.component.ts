import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { LoadChainsAction } from './chain-list-page.actions';
import * as fromActions from './chain-list-page.actions';
import { ChainListPageState, getChains } from './chain-list-page.reducers';
import { ChainModel, ChainOperationalModel } from './chain.model';

@Component({
  selector: 'app-chain-list-page',
  templateUrl: './chain-list-page.component.html',
  styleUrls: ['./chain-list-page.component.scss']
})
export class ChainListPageComponent implements OnInit {
  isChainModalVisible = false;
  isOkLoading = false;

  chains$: Observable<ChainModel[]>;

  constructor(
    private store: Store<ChainListPageState>,
    private fb: FormBuilder,
    ) {
    store.dispatch(new LoadChainsAction());
    this.chains$ = store.pipe(select(getChains));
  }

  newChainForm: FormGroup;

  get chainName() {
    return this.newChainForm.get('chainName') as FormControl;
  }

  showAddChainModal(): void {
    this.isChainModalVisible = true;
  }

  pushChain(): void {
    const chainData: ChainOperationalModel = { name: this.chainName.value };
    this.store.dispatch(new fromActions.CreateChainAction(chainData));
    this.isChainModalVisible = false;
  }

  handleCancel(): void {
    this.isChainModalVisible = false;
  }

  ngOnInit() {
    this.newChainForm = this.fb.group({
      chainName: new FormControl('', [Validators.required, Validators.minLength(3)]),
    });
  }

}
