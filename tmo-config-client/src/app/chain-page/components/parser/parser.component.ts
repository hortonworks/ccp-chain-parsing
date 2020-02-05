import cloneDeep from 'clone-deep';
import { debounce } from 'debounce';

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
  @Output() removeParser = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<any>();

  areFormsReadyToRender = false;

  ngOnInit() {

    this.configForm = this.setFormFieldValues(cloneDeep(this.configForm));
    this.outputsForm = this.setFormFieldValues(cloneDeep(this.outputsForm));

    this.configForm = this.addFormFieldListeners(cloneDeep(this.configForm));
    this.outputsForm = this.addFormFieldListeners(cloneDeep(this.outputsForm));

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
    return fields.map(field => {
      if (field.name === key) {
        return {
          ...field,
          value: this.parser[key]
        };
      }
      return field;
    });
  }

  setFormFieldValues(fields = []) {
    return fields.map(field => {
      if (this.parser[field.name] !== undefined) {
        return {
          ...field,
          value: this.parser[field.name]
        };
      }
      return field;
    });
  }

  addFormFieldListeners(fields = []) {
    return fields.map(field => {
      return {
        ...field,
        onChange: debounce((formFieldData) => {
          this.dirty = true;
          this.parserChange.emit({
            id: this.parser.id,
            [formFieldData.name]: formFieldData.value
          });
        }, 400)
      };
    });
  }

  onRemoveParser(parserId: string) {
    this.removeParser.emit(parserId);
  }

}
