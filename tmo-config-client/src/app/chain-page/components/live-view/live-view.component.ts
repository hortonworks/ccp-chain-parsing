import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { combineLatest, Observable, Subject } from 'rxjs';
import { debounceTime, filter, takeUntil } from 'rxjs/operators';

import { getChainDetails } from '../../chain-page.reducers';

import { executionTriggered } from './live-view.actions';
import { LiveViewState } from './live-view.reducers';
import { getExecutionStatus, getSampleData } from './live-view.selectors';
import { SampleDataModel } from './models/sample-data.model';

@Component({
  selector: 'app-live-view',
  templateUrl: './live-view.component.html',
  styleUrls: ['./live-view.component.scss']
})
export class LiveViewComponent implements OnDestroy {
  readonly LIVE_VIEW_DEBOUNCE_RATE = 1000;

  private unsubscribe$: Subject<void> = new Subject<void>();

  isExecuting$: Observable<boolean>;
  sampleData$: Observable<SampleDataModel>;

  sampleDataChange$ = new Subject<SampleDataModel>();

  constructor(private store: Store<LiveViewState>, private activatedRoute: ActivatedRoute) {
    this.isExecuting$ = this.store.pipe(select(getExecutionStatus));
    this.sampleData$ = this.store.pipe(select(getSampleData));

    this.activatedRoute.params.subscribe((params) => {
      this.subscribeToRelevantChanges(params.id);
    });
  }

  private subscribeToRelevantChanges(chainId: string) {
    combineLatest([
      this.sampleDataChange$,
      this.store.pipe(select(getChainDetails, { chainId })),
    ]).pipe(
      debounceTime(this.LIVE_VIEW_DEBOUNCE_RATE),
      filter(([ sampleData ]) => !!sampleData.source),
      takeUntil(this.unsubscribe$)
    ).subscribe(([ sampleData, chainConfig ]) => {
      this.store.dispatch(executionTriggered({ sampleData, chainConfig }));
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
