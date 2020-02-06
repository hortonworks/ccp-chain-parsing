import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ParserModel, PartialParserModel } from '../../chain-page.models';
import { CustomFormConfig } from '../custom-form/custom-form.component';

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

  configFormFields: CustomFormConfig[] = [{
    id: '123',
    name: 'config',
    type: 'textarea',
  }];

  outputsFormFields: CustomFormConfig[] = [{
    id: '456',
    name: 'outputs',
    type: 'textarea',
  }];

  metaDataForm: CustomFormConfig[] = [{
    id: 'name',
    name: 'name',
    type: 'text',
    label: 'Name'
  }, {
    id: 'type',
    name: 'type',
    type: 'select',
    label: 'Type',
    options: [{
      id: 'Grok',
      name: 'Grok'
    }, {
      id: 'foo',
      name: 'Foo'
    }, {
      id: 'bar',
      name: 'Bar'
    }, {
      id: 'Router',
      name: 'Router'
    }]
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

}
