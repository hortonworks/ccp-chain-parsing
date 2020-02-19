import { AfterViewInit, Component, Input, OnDestroy } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { combineLatest, Observable, Subject } from 'rxjs';
import { debounceTime, filter, takeUntil } from 'rxjs/operators';

import {
  executionTriggered,
  liveViewInitialized,
  onOffToggleChanged,
  sampleDataInputChanged,
} from './live-view.actions';
import { LiveViewConsts } from './live-view.consts';
import { LiveViewState } from './live-view.reducers';
import {
  getExecutionStatus,
  getIsLiveViewOn,
  getResults,
  getSampleData,
} from './live-view.selectors';
import { LiveViewResultModel } from './models/live-view.model';
import { SampleDataModel } from './models/sample-data.model';

@Component({
  selector: 'app-live-view',
  templateUrl: './live-view.component.html',
  styleUrls: ['./live-view.component.scss']
})
export class LiveViewComponent implements AfterViewInit, OnDestroy {
  @Input() chainConfig$: Observable<{}>;

  private unsubscribe$: Subject<void> = new Subject<void>();

  isLiveViewOn$: Observable<boolean>;
  isExecuting$: Observable<boolean>;
  results$: Observable<LiveViewResultModel>;
  sampleData$: Observable<SampleDataModel>;

  sampleDataChange$ = new Subject<SampleDataModel>();
  featureToggleChange$ = new Subject<boolean>();

  constructor(private store: Store<LiveViewState>) {
    this.isExecuting$ = this.store.pipe(select(getExecutionStatus));
    this.results$ = this.store.pipe(select(getResults));
    this.sampleData$ = this.store.pipe(select(getSampleData));
    this.isLiveViewOn$ = this.store.pipe(select(getIsLiveViewOn));
  }

  ngOnInit() {
    this.store.dispatch(liveViewInitialized());
  }

  ngAfterViewInit() {
    this.subscribeToRelevantChanges();
  }

  private subscribeToRelevantChanges() {
    combineLatest([
      this.sampleData$,
      this.chainConfig$,
      this.isLiveViewOn$,
    ]).pipe(
      debounceTime(LiveViewConsts.LIVE_VIEW_DEBOUNCE_RATE),
      filter(([ sampleData, chainConfig, isLiveViewOn ]) => isLiveViewOn && !!sampleData.source),
      takeUntil(this.unsubscribe$)
    ).subscribe(([ sampleData, chainConfig ]) => {
      this.store.dispatch(executionTriggered({ sampleData, chainConfig }));
    });

    this.featureToggleChange$.pipe(
        takeUntil(this.unsubscribe$),
        filter(value => value !== null),
      ).subscribe(value => {
      this.store.dispatch(onOffToggleChanged({ value }));
    });

    this.sampleDataChange$.pipe(
      takeUntil(this.unsubscribe$),
      filter(sampleData => sampleData !== null),
    ).subscribe(sampleData => {
      this.store.dispatch(sampleDataInputChanged({ sampleData }));
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
