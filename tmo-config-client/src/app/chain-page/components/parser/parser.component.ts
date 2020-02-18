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
  @Input() metaDataForm: CustomFormConfig[];
  @Input() configForm: CustomFormConfig[];
  @Input() outputsForm: CustomFormConfig[];
  @Output() removeParser = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<any>();

  areFormsReadyToRender = false;

  ngOnInit() {
    this.configForm = this.setFormFieldValues(this.configForm);
    this.outputsForm = this.setFormFieldValues(this.outputsForm);
    this.metaDataForm = this.setFormFieldValues(this.metaDataForm);

    this.configForm = this.addFormFieldListeners(this.configForm);
    this.outputsForm = this.addFormFieldListeners(this.outputsForm);
    this.metaDataForm = this.addFormFieldListeners(this.metaDataForm);

    setTimeout(() => {
      this.areFormsReadyToRender = true;
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.configForm) {
      this.areFormsReadyToRender = false;
      this.configForm = this.setFormFieldValues(this.configForm);
      this.configForm = this.addFormFieldListeners(this.configForm);
      setTimeout(() => {
        this.areFormsReadyToRender = true;
      });
    }
    if (changes.parser && changes.parser.previousValue) {
      Object.keys(changes.parser.previousValue).forEach(key => {
        if (changes.parser.previousValue[key] !== changes.parser.currentValue[key]) {
          if (key === 'config') {
            this.configForm = this.updateFormValues(key, this.configForm);
          }
          if (key === 'outputs') {
            this.outputsForm = this.updateFormValues(key, this.outputsForm);
          }
          if (['name', 'type'].includes(key)) {
            this.metaDataForm = this.updateFormValues(key, this.metaDataForm);
          }
        }
      });
    }
  }

  updateFormValues(key, fields = []) {
    return produce(fields, (draft) => {
      draft.forEach(field => {
        if (
          field.path
          && field.path === key
          && typeof this.parser[field.path] === 'object') {
          const keys = Object.keys(this.parser[field.path]);
          if (keys.length === 0) {
            field.value = field.defaultValue || '';
          } else {
            keys.forEach((k) => {
              if (k === field.name) {
                field.value = this.parser[field.path][k];
              }
            });
          }
        } else if (field.name === key) {
          field.value = this.parser[key];
        }
      });
    });
  }

  setFormFieldValues(fields = []) {
    return produce(fields, (draft) => {
      draft.forEach(field => {
        if (field.path && this.parser[field.path] !== undefined && this.parser[field.path][field.name] !== undefined) {
          field.value = this.parser[field.path][field.name];
        } else if (this.parser[field.name] !== undefined) {
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
          const partialParser = formFieldData.path
            ? {
              id: this.parser.id,
              [formFieldData.path]: {
                ...(this.parser[formFieldData.path] || {}),
                [formFieldData.name]: formFieldData.value
              }
            }
            : {
              id: this.parser.id,
              [formFieldData.name]: formFieldData.value
            };
          this.parserChange.emit(partialParser);
        }, 400);
      });
    });
  }

  onRemoveParser(parserId: string) {
    this.removeParser.emit(parserId);
  }

}
