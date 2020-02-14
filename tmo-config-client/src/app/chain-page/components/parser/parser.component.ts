import { debounce } from 'debounce';
import produce from 'immer';

import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';

import { ParserModel } from '../../chain-page.models';
import { CustomFormConfig } from '../custom-form/custom-form.component';

@Component({
  selector: 'app-parser',
  templateUrl: './parser.component.html',
  styleUrls: ['./parser.component.scss']
})
export class ParserComponent implements OnInit, OnChanges {

  @Input() dirty = false;
  @Input() parser: ParserModel;
  @Input() configForm: CustomFormConfig[];
  @Input() outputsForm: CustomFormConfig[];
  @Input() investigated = false;
  @Output() removeParser = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<any>();

  areFormsReadyToRender = false;

  ngOnInit() {

    this.configForm = this.setFormFieldValues(this.configForm);
    this.outputsForm = this.setFormFieldValues(this.outputsForm);

    this.configForm = this.addFormFieldListeners(this.configForm);
    this.outputsForm = this.addFormFieldListeners(this.outputsForm);

    setTimeout(() => {
      this.areFormsReadyToRender = true;
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.parser && changes.parser.previousValue) {
      Object.keys(changes.parser.previousValue).forEach(key => {
        if (changes.parser.previousValue[key] !== changes.parser.currentValue[key]) {
          if (key === 'config') {
            this.configForm = this.updateFormValues(key, this.configForm);
          }
          if (key === 'outputs') {
            this.outputsForm = this.updateFormValues(key, this.outputsForm);
          }
        }
      });
    }
  }

  updateFormValues(key, fields = []) {
    return produce(fields, (draft) => {
      draft.forEach(field => {
        if (field.name === key) {
          field.value = this.parser[key];
        }
      });
    });
  }

  setFormFieldValues(fields = []) {
    return produce(fields, (draft) => {
      draft.forEach(field => {
        if (this.parser[field.name] !== undefined) {
          field.value = this.parser[field.name];
        }
      });
    });
  }

  addFormFieldListeners(fields = []) {
    return produce(fields, (draft) => {
      draft.forEach(field => {
        field.onChange = debounce((formFieldData) => {
          this.dirty = true;
          this.parserChange.emit({
            id: this.parser.id,
            [formFieldData.name]: formFieldData.value
          });
        }, 400);
      });
    });
  }

  onRemoveParser(parserId: string) {
    this.removeParser.emit(parserId);
  }

}
