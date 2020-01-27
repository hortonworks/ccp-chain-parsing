import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ChainDetailsModel, ParserModel } from '../../chain-details.model';

@Component({
  selector: 'app-router',
  templateUrl: './router.component.html',
  styleUrls: ['./router.component.scss']
})
export class RouterComponent implements OnInit {

  @Input() parser: ParserModel;
  @Output() chainItemSelected = new EventEmitter<ChainDetailsModel>();
  @Output() removeParser = new EventEmitter<string>();

  constructor() {
  }

  ngOnInit() {
  }

  onSubchainItemClick(event: Event, chain: ChainDetailsModel) {
    event.preventDefault();
    this.chainItemSelected.emit(chain);
  }

  onRemoveParser(id: string) {
    this.removeParser.emit(id);
  }

  getParserNamesArray(parsers: ParserModel[]): string[] {
    return parsers.map(p => p.name);
  }

}
