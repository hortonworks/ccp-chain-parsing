import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ChainDetailsModel, ParserModel } from '../../chain-details.model';

@Component({
  selector: 'app-chain-view',
  templateUrl: './chain-view.component.html',
  styleUrls: ['./chain-view.component.scss']
})
export class ChainViewComponent implements OnInit {

  @Input() parsers: ParserModel[];
  @Output() removeParserEmitter = new EventEmitter<string>();
  @Output() chainLevelChange = new EventEmitter<ChainDetailsModel>();

  constructor() { }

  ngOnInit() {
  }

  removeParser(id: string) {
    this.removeParserEmitter.emit(id);
  }

  onChainItemSelected(chain: ChainDetailsModel) {
    this.chainLevelChange.emit(chain);
  }

}
