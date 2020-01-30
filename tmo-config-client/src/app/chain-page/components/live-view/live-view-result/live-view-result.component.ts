import { Component, Input, OnInit } from '@angular/core';

import { LiveViewResultModel } from '../models/live-view.model';

@Component({
  selector: 'app-live-view-result',
  templateUrl: './live-view-result.component.html',
  styleUrls: ['./live-view-result.component.scss']
})
export class LiveViewResultComponent implements OnInit {
  @Input() results: LiveViewResultModel;

  constructor() { }

  ngOnInit() {
  }

}
