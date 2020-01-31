import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { ChainPageState, getParser } from '../../chain-page.reducers';

import * as fromActions from '../../chain-page.actions';
import { ParserModel } from '../../chain-page.models';

@Component({
  selector: 'app-parser-composer',
  templateUrl: './parser-composer.component.html',
  styleUrls: ['./parser-composer.component.scss']
})
export class ParserComposerComponent implements OnInit {

  @Input() parserId: string;
  @Input() configForm: any;
  @Input() outputsForm: any;
  @Output() subchainSelect = new EventEmitter<string>();
  @Output() parserRemove = new EventEmitter<string>();

  parser: ParserModel;

  constructor(
    private store: Store<ChainPageState>
  ) { }

  ngOnInit() {
    this.store.pipe(select(getParser, {
      id: this.parserId
    })).subscribe((parser) => {
      this.parser = parser;
    });
  }

  onSubchainSelect(chainId: string) {
    this.subchainSelect.emit(chainId);
  }

  onParserChange(partialParser) {
    this.store.dispatch(
      new fromActions.UpdateParserAction({
        parser: partialParser
      })
    );
  }

  onRemoveParser(parserId: string) {
    this.parserRemove.emit(parserId);
  }

}
