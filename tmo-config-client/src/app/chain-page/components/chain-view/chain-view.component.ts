import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ParserModel } from '../../chain-details.model';

@Component({
  selector: 'app-chain-view',
  templateUrl: './chain-view.component.html',
  styleUrls: ['./chain-view.component.scss']
})
export class ChainViewComponent implements OnInit {

  @Input() parsers: ParserModel[];
  @Output() removeParserEmitter = new EventEmitter<string>();
  @Output() chainLevelChange = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<ParserModel>();

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

  onParserChange(parser: ParserModel) {
    this.parserChange.emit(parser);
  }

  onParserRemove(parserId: string) {
    this.removeParserEmitter.emit(parserId);
  }

}
