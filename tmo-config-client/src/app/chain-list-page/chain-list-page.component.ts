import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { LoadChainsAction } from './chain-list-page.actions';
import * as fromActions from './chain-list-page.actions';
import { ChainListPageState, getChains } from './chain-list-page.reducers';
import { ChainModel, ChainOperationalModel } from './chain.model';

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;
const defaults = {
  page: DEFAULT_PAGE,
  pageSize: DEFAULT_PAGE_SIZE
};
@Component({
  selector: 'app-chain-list-page',
  templateUrl: './chain-list-page.component.html',
  styleUrls: ['./chain-list-page.component.scss']
})
export class ChainListPageComponent implements OnInit {
  isChainModalVisible = false;
  isOkLoading = false;
  pageNumber: number;
  pageSize: number;
  chains$: Observable<ChainModel[]>;
  totalRecords = 200;
  chainDataSorted$: Observable<ChainModel[]>;
  sortDescription$: BehaviorSubject<{key: string, value: string}> = new BehaviorSubject({ key: 'name', value: '' });
  constructor(
    private store: Store<ChainListPageState>,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    ) {
    this.route.queryParams.subscribe((params: ParamMap) => {
      const param = Object.assign({}, defaults, params || {});
      this.pageNumber = param.page;
      this.pageSize = param.pageSize;

      store.dispatch(new LoadChainsAction({page: this.pageNumber, pageSize: this.pageSize}));
      this.chains$ = store.pipe(select(getChains));
    });

    this.chainDataSorted$ = combineLatest([
      this.chains$,
      this.sortDescription$
    ]).pipe(
      switchMap(([ chains, sortDescription ]) => this.sortTable(chains, sortDescription))
    );
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

  deleteChain(event: Event, chainId: string): void {
    this.store.dispatch(new fromActions.DeleteChainAction(chainId));
  }
  handleCancel(): void {
    this.isChainModalVisible = false;
  }

  setPageSize(value): void {
    this.pageSize = value;
    this.setPagination();
  }
  setPageNumber(value): void {
    this.pageNumber = value;
    this.setPagination();
  }

  setPagination(): void {
    this.router.navigate(['/parserconfig'], {
      queryParams: {
        page: this.pageNumber,
        pageSize : this.pageSize
      }
    });
  }

  sortTable(data, sortDescription): Observable<[]> {
    const sortValue = sortDescription.value;
    const newData = data.slice().sort((a, b): number => {
      const first = a.name.toLowerCase();
      const second = b.name.toLowerCase();
      if (sortValue === 'ascend') {
        return (first < second) ? -1 : 1;
      } else if (sortValue === 'descend') {
        return (first < second) ? 1 : -1;
      } else {
        return 0;
      }
    });
    return of(newData);
  }

  ngOnInit() {
    this.newChainForm = this.fb.group({
      chainName: new FormControl('', [Validators.required, Validators.minLength(3)]),
    });
  }
}
