import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ParserModel } from '../../chain-details.model';

@Component({
  selector: 'app-router',
  templateUrl: './router.component.html',
  styleUrls: ['./router.component.scss']
})
export class RouterComponent implements OnInit {

  @Input() parser: ParserModel;
  @Output() subchainSelect = new EventEmitter<string>();
  @Output() removeParser = new EventEmitter<string>();

  constructor() {
  }

  ngOnInit() {
  }

  onSubchainClick(chainId: string) {
    this.subchainSelect.emit(chainId);
  }

  onRemoveParser(id: string) {
    this.removeParser.emit(id);
  }
}
