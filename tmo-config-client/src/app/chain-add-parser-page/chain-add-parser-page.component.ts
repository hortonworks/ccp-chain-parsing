import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';


@Component({
  selector: 'app-chain-add-parser-page',
  templateUrl: './chain-add-parser-page.component.html',
  styleUrls: ['./chain-add-parser-page.component.scss']
})
export class ChainAddParserPageComponent implements OnInit {
  addParserForm: FormGroup;
  typesList = ['abc', 'def', 'ghi', 'jkl'];
  parsersList: string[] = [];
  isFirstParser: boolean;
  constructor(
    private fb: FormBuilder
  ) {
    this.parsersList = ['parser1', 'parser2', 'parser3', 'parser4', 'parser5'];
  }

  get name() {
    return this.addParserForm.get('name') as FormControl;
  }
  get type() {
    return this.addParserForm.get('type') as FormControl;
  }
  get sourceParser() {
    return this.addParserForm.get('sourceParser') as FormControl;
  }
  get sourceParserOutput() {
    return this.addParserForm.get('sourceParserOutput') as FormControl;
  }

  addParser() {
    // const formValues = this.addParserForm.value;
    this.isFirstParser = true;
    // const data = {value: formValues, isFirstParser: this.isFirstParser};
  }

  ngOnInit() {
    this.addParserForm = this.fb.group({
      name: new FormControl('', [Validators.required, Validators.minLength(3)]),
      type: new FormControl(null, [Validators.required])
    });
    if (this.parsersList.length) {
      this.addParserForm.addControl('sourceParser', new FormControl(null, [Validators.required]));
      this.addParserForm.addControl('sourceParserOutput', new FormControl('', [Validators.required, Validators.minLength(3)]));
    }
  }
}
