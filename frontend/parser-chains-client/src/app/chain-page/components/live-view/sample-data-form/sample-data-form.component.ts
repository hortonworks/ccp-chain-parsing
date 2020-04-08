import { Component, EventEmitter, Input, Output } from '@angular/core';

import { SampleDataModel } from '../models/sample-data.model';

@Component({
  selector: 'app-sample-data-form',
  templateUrl: './sample-data-form.component.html',
  styleUrls: ['./sample-data-form.component.scss']
})
export class SampleDataFormComponent {

  @Input() sampleData: SampleDataModel;
  @Output() sampleDataChange = new EventEmitter<SampleDataModel>();

  onApply(sampleDataInput: string) {
    this.sampleDataChange.emit({
      ...this.sampleData,
      source: sampleDataInput
    });
  }

}
