import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { ChainPageState, getFormConfigByType, getParser } from '../../chain-page.reducers';

import * as fromActions from '../../chain-page.actions';
import { ParserModel, PartialParserModel } from '../../chain-page.models';
import { CustomFormConfig } from '../custom-form/custom-form.component';

@Component({
  selector: 'app-parser-composer',
  templateUrl: './parser-composer.component.html',
  styleUrls: ['./parser-composer.component.scss']
})
export class ParserComposerComponent implements OnInit {

  @Input() dirty = false;
  @Input() parserId: string;
  @Input() chainId: string;
  @Input() outputsForm: CustomFormConfig[];
  @Input() metaDataForm: CustomFormConfig[];
  @Output() subchainSelect = new EventEmitter<string>();
  @Output() parserRemove = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<PartialParserModel>();

  configForm: CustomFormConfig[];
  parser: ParserModel;

  constructor(
    private store: Store<ChainPageState>
  ) { }

  ngOnInit() {
    this.store.pipe(select(getParser, {
      id: this.parserId
    })).subscribe((parser) => {
      this.parser = parser;
      if (parser) {
        this.store.pipe(select(getFormConfigByType, { type: parser.type }))
        .subscribe((formConfig) => {
          this.configForm = formConfig;
        });
      }
    });
  }

  onSubchainSelect(chainId: string) {
    this.subchainSelect.emit(chainId);
  }

  onParserChange(partialParser) {
    this.store.dispatch(
      new fromActions.UpdateParserAction({
        chainId: this.chainId,
        parser: partialParser
      })
    );
    this.parserChange.emit(partialParser);
  }

  onRemoveParser(parserId: string) {
    this.parserRemove.emit(parserId);
  }

}
