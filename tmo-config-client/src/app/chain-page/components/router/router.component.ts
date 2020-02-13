import { Component, EventEmitter, Input, Output } from '@angular/core';

import { ParserModel } from '../../chain-page.models';
import { ParserComponent } from '../parser/parser.component';

@Component({
  selector: 'app-router',
  templateUrl: './router.component.html',
  styleUrls: ['./router.component.scss']
})
export class RouterComponent extends ParserComponent {

  @Input() dirty = false;
  @Input() parser: ParserModel;
  @Output() subchainSelect = new EventEmitter<string>();

  onSubchainClick(chainId: string) {
    this.subchainSelect.emit(chainId);
  }
}
