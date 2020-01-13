import { Component, Input, OnInit } from '@angular/core';

import { ParserModel } from '../../chain-details.model';

@Component({
  selector: 'app-chain-view',
  templateUrl: './chain-view.component.html',
  styleUrls: ['./chain-view.component.scss']
})
export class ChainViewComponent implements OnInit {

  @Input() parsers: ParserModel[];

  constructor() { }

  ngOnInit() {
  }

}
