import { Component, EventEmitter, Input, Output } from '@angular/core';

import { ParserModel } from '../../chain-page.models';

@Component({
  selector: 'app-router',
  templateUrl: './router.component.html',
  styleUrls: ['./router.component.scss']
})
export class RouterComponent {

  @Input() dirty = false;
  @Input() parser: ParserModel;
  @Output() subchainSelect = new EventEmitter<string>();
  @Output() removeParser = new EventEmitter<string>();

  onSubchainClick(chainId: string) {
    this.subchainSelect.emit(chainId);
  }

  onRemoveParser(id: string) {
    this.removeParser.emit(id);
  }
}
