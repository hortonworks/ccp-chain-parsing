import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { NzModalService } from 'ng-zorro-antd';
import { Observable, Observer } from 'rxjs';
import { take } from 'rxjs/operators';

import { DeactivatePreventer } from '../misc/deactivate-preventer.interface';

import * as fromActions from './chain-page.actions';
import { ChainDetailsModel, ParserChainModel, PartialParserModel } from './chain-page.models';
import { ChainPageState, getChain, getChainDetails, isDirty } from './chain-page.reducers';

class DirtyChain {
  id: string;
  parsers: string[] = [];
  constructor(id: string) {
    this.id = id;
  }
}

@Component({
  selector: 'app-chain-page',
  templateUrl: './chain-page.component.html',
  styleUrls: ['./chain-page.component.scss']
})
export class ChainPageComponent implements OnInit, DeactivatePreventer {

  chain: ParserChainModel;
  breadcrumbs: ParserChainModel[] = [];
  chainId: string;
  dirty = false;
  dirtyChains: { [key: string]: DirtyChain } = {};
  chainConfig$: Observable<ChainDetailsModel>;

  constructor(
    private store: Store<ChainPageState>,
    private activatedRoute: ActivatedRoute,
    private modal: NzModalService,
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

    this.chainConfig$ = this.store.pipe(select(getChainDetails, { chainId: this.chainId }));

    this.store.pipe(select(isDirty)).subscribe((dirty) => {
      this.dirty = dirty;
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
    this.store.dispatch(new fromActions.SetDirtyAction({
      dirty: true
    }));
  }

  onParserChange(changedParser: PartialParserModel) {
    this.store.dispatch(new fromActions.SetDirtyAction({
      dirty: true
    }));
    if (this.breadcrumbs.length > 0) {
      this.breadcrumbs.forEach((chain: ParserChainModel) => {
        if (chain.parsers && chain.parsers.length > 0) {
          const parserId = (chain.parsers as string[]).find((pid) => pid === changedParser.id);
          if (parserId) {
            if (!this.dirtyChains[chain.id]) {
              this.dirtyChains[chain.id] = new DirtyChain(chain.id);
            }
            if (!this.dirtyChains[chain.id].parsers.includes(parserId)) {
              this.dirtyChains[chain.id].parsers.push(parserId);
            }
          }
        }
      });
    }
  }

  onChainLevelChange(chainId: string) {
    this.store.pipe(select(getChain, { id: chainId })).pipe(take(1)).subscribe((chain: ParserChainModel) => {
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

  canDeactivate(): Observable<boolean> {
    const allow = (o: Observer<boolean>) => { o.next(true); o.complete(); };
    const deny  = (o: Observer<boolean>) => { o.next(false); o.complete(); };

    return new Observable((observer: Observer<boolean>) => {
      if (this.dirty) {
        this.modal.confirm({
          nzTitle: 'You have unsaved changes',
          nzContent: 'Are you sure you want to leave this page?',
          nzOkText: 'Leave',
          nzOkType: 'danger',
          nzCancelText: 'Cancel',
          nzOnOk: () => allow(observer),
          nzOnCancel: () => deny(observer),
        });
      } else {
        allow(observer);
      }
    });
  }

  onResetChanges() {
    this.modal.confirm({
      nzTitle: 'Your changes will be lost',
      nzContent: 'Are you sure you want to reset?',
      nzOkText: 'Reset',
      nzOkType: 'danger',
      nzCancelText: 'Cancel',
      nzOnOk: () => {
        this.store.dispatch(new fromActions.SetDirtyAction({
          dirty: false
        }));
        Object.keys(this.dirtyChains).forEach(chainId => {
          this.dirtyChains[chainId].parsers = [];
        });
        this.store.dispatch(new fromActions.LoadChainDetailsAction({
          id: this.chainId
        }));
      }
    });
  }

  onSaveChanges() {
    this.modal.confirm({
      nzTitle: 'You are about the save your changes',
      nzContent: 'Are you sure you want to save?',
      nzOkText: 'Save',
      nzOkType: 'primary',
      nzCancelText: 'Cancel',
      nzOnOk: () => {
        this.store.dispatch(new fromActions.SetDirtyAction({
          dirty: false
        }));
        Object.keys(this.dirtyChains).forEach(chainId => {
          this.dirtyChains[chainId].parsers = [];
        });
        this.store.dispatch(new fromActions.SaveParserConfigAction({ chainId: this.chainId }));
      }
    });
  }
}
