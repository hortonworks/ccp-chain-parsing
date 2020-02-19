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
  @Input() chainId: string;
  @Output() removeParserEmitter = new EventEmitter<string>();
  @Output() chainLevelChange = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<PartialParserModel>();

  outputsFormFields: CustomFormConfig[] = [{
    id: '456',
    name: 'outputs',
    type: 'textarea',
  }];

  metaDataForm: CustomFormConfig[] = [{
    id: 'name',
    name: 'name',
    type: 'text',
    label: 'Name',
    placeholder: 'Choose a parser type'
  }, {
    id: 'type',
    name: 'type',
    type: 'select',
    label: 'Type',
    options: [{
      id: 'Syslog',
      name: 'Syslog'
    }, {
      id: 'RenameField',
      name: 'Rename Field'
    }, {
      id: 'RemoveField',
      name: 'Remove Field'
    }, {
      id: 'Timestamp',
      name: 'Timestamp'
    }, {
      id: 'DelimitedText',
      name: 'Delimited Text'
    }, {
      id: 'Error',
      name: 'Error'
    }]
  }];

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
