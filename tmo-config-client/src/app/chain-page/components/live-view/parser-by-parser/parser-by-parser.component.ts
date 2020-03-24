import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-parser-by-parser',
  templateUrl: './parser-by-parser.component.html',
  styleUrls: ['./parser-by-parser.component.scss']
})
export class ParserByParserComponent implements OnInit {
  @Input() parserResults: any;

  constructor() { }

  ngOnInit() {
  }

}
