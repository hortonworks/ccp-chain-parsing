import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { NzModalService } from 'ng-zorro-antd';
import { Observable, Observer, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';

import { DeactivatePreventer } from '../misc/deactivate-preventer.interface';

import * as fromActions from './chain-page.actions';
import { ChainDetailsModel, ParserChainModel, PartialParserModel } from './chain-page.models';
import { ChainPageState, getChain, getChainDetails, getChains, isDirty } from './chain-page.reducers';

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
export class ChainPageComponent implements OnInit, OnDestroy, DeactivatePreventer {

  chain: ParserChainModel;
  breadcrumbs: ParserChainModel[] = [];
  chainId: string;
  dirty = false;
  dirtyChains: { [key: string]: DirtyChain } = {};
  chainConfig$: Observable<ChainDetailsModel>;
  chainIdBeingEdited: string;
  getChainsSubscription: Subscription;
  popOverVisible = false;
  @ViewChild('chainNameInput', { static: false }) chainNameInput: ElementRef;
  editChainNameForm: FormGroup;
  constructor(
    private store: Store<ChainPageState>,
    private activatedRoute: ActivatedRoute,
    private modal: NzModalService,
    private fb: FormBuilder,
  ) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params) => {
      this.chainId = params.id;
      this.store.dispatch(new fromActions.LoadChainDetailsAction({
        id: params.id
      }));
    });

    this.getChainsSubscription = this.store.pipe(select(getChains)).subscribe((chains) => {
      this.breadcrumbs = this.breadcrumbs.map(breadcrumb => {
        return chains[breadcrumb.id];
      });
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

    this.editChainNameForm = this.fb.group({
      name: new FormControl(null, [Validators.required, Validators.minLength(3)])
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

  onBreadcrumbEditClick(event: Event, chain: ParserChainModel) {
    event.preventDefault();
    this.editChainNameForm.get('name').setValue(chain.name);
    setTimeout(() => {
      this.chainNameInput.nativeElement.focus();
    });
  }

  onBreadcrumbEditDone(chain: ParserChainModel) {
    this.popOverVisible = false;
    const value = ((this.chainNameInput.nativeElement as HTMLInputElement).value || '').trim();
    if (value !== chain.name) {
      this.store.dispatch(new fromActions.UpdateChainAction({
        chain: {
          id: chain.id,
          name: value
        }
      }));
      this.store.dispatch(new fromActions.SetDirtyAction({
        dirty: true
      }));
      if (!this.dirtyChains[chain.id]) {
        this.dirtyChains[chain.id] = new DirtyChain(chain.id);
      }
    }
  }

  onBreadcrumbEditCancel() {
    this.popOverVisible = false;
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
          delete this.dirtyChains[chainId];
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
          delete this.dirtyChains[chainId];
        });
        this.store.dispatch(new fromActions.SaveParserConfigAction({ chainId: this.chainId }));
      }
    });
  }

  ngOnDestroy() {
    this.breadcrumbs = [];
    if (this.getChainsSubscription) {
      this.getChainsSubscription.unsubscribe();
    }
  }
}
