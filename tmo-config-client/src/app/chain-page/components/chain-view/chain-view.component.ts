import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ParserModel, PartialParserModel } from '../../chain-page.models';

@Component({
  selector: 'app-chain-view',
  templateUrl: './chain-view.component.html',
  styleUrls: ['./chain-view.component.scss']
})
export class ChainViewComponent implements OnInit {

  @Input() parsers: ParserModel[];
  @Input() dirtyParsers: string[];
  @Output() removeParserEmitter = new EventEmitter<string>();
  @Output() chainLevelChange = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<PartialParserModel>();

  configFormFields = [{
    id: '123',
    name: 'config',
    type: 'textarea',
  }];

  outputsFormFields = [{
    id: '456',
    name: 'outputs',
    type: 'textarea',
  }];

  constructor() { }

  ngOnInit() {
  }

  removeParser(id: string) {
    this.removeParserEmitter.emit(id);
  }

  onChainSelected(chainId: string) {
    this.chainLevelChange.emit(chainId);
  }

  onParserChange(parser: PartialParserModel) {
    this.parserChange.emit(parser);
  }

  onParserRemove(parserId: string) {
    this.removeParserEmitter.emit(parserId);
  }

  trackByFn(index: number, parserId: string): string {
    return parserId;
  }

}
