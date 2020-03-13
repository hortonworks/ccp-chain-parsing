import { AfterViewInit, Component, EventEmitter, Input, Output } from '@angular/core';

import { ParserModel, PartialParserModel } from '../../chain-page.models';

import { ChainPageService } from './../../../services/chain-page.service';

@Component({
  selector: 'app-chain-view',
  templateUrl: './chain-view.component.html',
  styleUrls: ['./chain-view.component.scss']
})
export class ChainViewComponent implements AfterViewInit {

  @Input() parsers: ParserModel[];
  @Input() dirtyParsers: string[];
  @Input() chainId: string;
  @Output() removeParserEmitter = new EventEmitter<string>();
  @Output() chainLevelChange = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<PartialParserModel>();

  parserCollapseStateArray: boolean[];
  constructor(private chainPageService: ChainPageService) {
  }
  ngAfterViewInit() {
    this.parserCollapseStateArray = new Array(this.parsers.length).fill(true);
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

  collapseAllParsers() {
    this.chainPageService.collapseAllParser();
    console.log('collapseAllParsers called');
  }

}
