import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ParserModel } from '../../chain-details.model';

@Component({
  selector: 'app-parser',
  templateUrl: './parser.component.html',
  styleUrls: ['./parser.component.scss']
})
export class ParserComponent implements OnInit {

  @Input() parser: ParserModel;
  @Output() removeParser = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
  }

  onRemoveParser(id: string) {
    this.removeParser.emit(id);
  }

}
