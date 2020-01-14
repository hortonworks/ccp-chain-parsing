import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';

import { ParserModel } from '../chain-page/chain-details.model';

import * as fromActions from './chain-add-parser-page.actions';
import { AddParserPageState, getParsers, getParserTypes } from './chain-add-parser-page.reducers';

@Component({
  selector: 'app-chain-add-parser-page',
  templateUrl: './chain-add-parser-page.component.html',
  styleUrls: ['./chain-add-parser-page.component.scss']
})
export class ChainAddParserPageComponent implements OnInit {
  addParserForm: FormGroup;
  typesList: { id: string, name: string }[] = [];
  parsersList: ParserModel[] = [];
  chainId: string;

  constructor(
    private fb: FormBuilder,
    private store: Store<AddParserPageState>,
    private activatedRoute: ActivatedRoute,
  ) {}

  get name() {
    return this.addParserForm.get('name') as FormControl;
  }
  get type() {
    return this.addParserForm.get('type') as FormControl;
  }
  get sourceParser() {
    return this.addParserForm.get('chainId') as FormControl;
  }
  get sourceParserOutput() {
    return this.addParserForm.get('outputs') as FormControl;
  }

  addParser() {
    this.store.dispatch(new fromActions.AddParserAction({
      chainId: this.chainId,
      parser: this.addParserForm.value
    }));
  }

  ngOnInit() {
    this.addParserForm = this.fb.group({
      name: new FormControl('', [Validators.required, Validators.minLength(3)]),
      type: new FormControl(null, [Validators.required])
    });

    this.activatedRoute.params.subscribe((params) => {
      this.chainId = params.id;
      this.store.dispatch(new fromActions.GetParserTypesAction());
      this.store.dispatch(new fromActions.GetParsersAction({
        chainId: params.id
      }));
    });

    this.store.pipe(select(getParserTypes)).subscribe((parserTypes) => {
      this.typesList = parserTypes;
    });

    this.store.pipe(select(getParsers)).subscribe((parsers) => {
      this.parsersList = parsers;

      if (this.parsersList.length) {
        this.addParserForm.addControl('parentId', new FormControl(null));
        this.addParserForm.addControl('outputs', new FormControl(''));
      }
    });



  }
}
