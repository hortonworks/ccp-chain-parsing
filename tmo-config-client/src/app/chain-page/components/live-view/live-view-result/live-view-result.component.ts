import { Component, EventEmitter, Input, Output } from '@angular/core';

import { LiveViewResultModel } from '../models/live-view.model';

@Component({
  selector: 'app-live-view-result',
  templateUrl: './live-view-result.component.html',
  styleUrls: ['./live-view-result.component.scss']
})
export class LiveViewResultComponent {
  @Input() results: LiveViewResultModel;
  @Output() failedParserSelected = new EventEmitter<string>();

  onFailedParserClicked(failedParser) {
    this.failedParserSelected.emit(failedParser);
  }
}
