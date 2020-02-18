import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { NzModalService } from 'ng-zorro-antd';
import { Observable, Observer, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';

import { DeactivatePreventer } from '../misc/deactivate-preventer.interface';

import * as fromActions from './chain-page.actions';
import { ChainDetailsModel, ParserChainModel, PartialParserModel } from './chain-page.models';
import { ChainPageState, getChain, getChainDetails, getDirtyStatus } from './chain-page.reducers';

@Component({
  selector: 'app-chain-page',
  templateUrl: './chain-page.component.html',
  styleUrls: ['./chain-page.component.scss']
})
export class ChainPageComponent implements OnInit, OnDestroy, DeactivatePreventer {

  chain: ParserChainModel;
  breadcrumbs: ParserChainModel[] = [];
  chainId: string;
  dirtyChains: string[] = [];
  dirtyParsers: string[] = [];
  chainConfig$: Observable<ChainDetailsModel>;
  getChainSubscription: Subscription;
  editMode = false;
  @ViewChild('chainNameInput', { static: false }) chainNameInput: ElementRef;
  forceDeactivate = false;

  constructor(
    private store: Store<ChainPageState>,
    private activatedRoute: ActivatedRoute,
    private modal: NzModalService,
    private router: Router
  ) { }

  get dirty() {
    return this.dirtyParsers.length || this.dirtyParsers;
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params) => {
      this.chainId = params.id;
    });

    this.getChainSubscription = this.store.pipe(select(getChain, { id: this.chainId })).subscribe((chain: ParserChainModel) => {
      if (!chain) {
        this.store.dispatch(new fromActions.LoadChainDetailsAction({
          id: this.chainId
        }));
      } else if (chain && chain.parsers && chain.parsers.length > 0) {
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

    this.store.pipe(select(getDirtyStatus)).subscribe((status) => {
      this.dirtyParsers = status.dirtyParsers;
      this.dirtyChains = status.dirtyChains;
    });

    this.chainConfig$ = this.store.pipe(select(getChainDetails, { chainId: this.chainId }));

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        if (event.url === `/parserconfig/chains/${this.chainId}/new`) {
          this.forceDeactivate = true;
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

  onParserChange(changedParser: PartialParserModel) {
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
      if (this.dirty && !this.forceDeactivate) {
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
        this.forceDeactivate = false;
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
        this.store.dispatch(new fromActions.SaveParserConfigAction({ chainId: this.chainId }));
      }
    });
  }

  ngOnDestroy() {
    if (this.getChainSubscription) {
      this.getChainSubscription.unsubscribe();
    }
  }

  updateChainName() {
    const newName: string = (this.chainNameInput.nativeElement.value || '').trim();
    if (newName !== this.chain.name) {
      this.store.dispatch(new fromActions.UpdateChainAction({
        chain: {
          name: newName,
          id: this.chain.id
        }
      }));
    }
    this.toggleEditMode();
  }

  toggleEditMode() {
    this.editMode = !this.editMode;
    if (this.editMode) {
      setTimeout(() => {
        this.chainNameInput.nativeElement.focus();
      }, 0);
    }
  }
}
