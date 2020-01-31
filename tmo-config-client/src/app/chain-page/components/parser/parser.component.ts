import cloneDeep from 'clone-deep';
import { debounce } from 'debounce';

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ParserModel } from '../../chain-page.models';
import { CustomFormConfig } from '../custom-form/custom-form.component';

@Component({
  selector: 'app-parser',
  templateUrl: './parser.component.html',
  styleUrls: ['./parser.component.scss']
})
export class ParserComponent implements OnInit {

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

  setFormFieldValues(fields = []) {
    return fields.map(field => {
      if (this.parser[field.name] !== undefined) {
        return {
          ...field,
          value: this.parser[field.name]
        };
      }
    });
  }

  addFormFieldListeners(fields = []) {
    return fields.map(field => {
      return {
        ...field,
        onChange: debounce((formFieldData) => {
          this.parserChange.emit({
            id: this.parser.id,
            [formFieldData.name]: formFieldData.value
          });
        }, 1000)
      };
    });
  }

  onRemoveParser(parserId: string) {
    this.removeParser.emit(parserId);
  }

}
