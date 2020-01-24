import { Component } from '@angular/core';
import { Store } from '@ngrx/store';

import { sampleDataChanged } from '../live-view.actions';
import { LiveViewState } from '../live-view.reducers';
import { SampleDataModel } from '../models/sample-data.model';

@Component({
  selector: 'app-sample-data-form',
  templateUrl: './sample-data-form.component.html',
  styleUrls: ['./sample-data-form.component.scss']
})
export class SampleDataFormComponent {

  sampleData = new SampleDataModel();

  constructor(private store: Store<LiveViewState>) {}

  onApply(sampleData: string) {
    this.sampleData.source = sampleData;
    this.store.dispatch(sampleDataChanged({ sampleData: this.sampleData }));
  }

}
