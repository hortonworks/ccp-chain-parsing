import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { debounce } from 'debounce';
import produce from 'immer';
import get from 'lodash.get';
import set from 'lodash.set';

import { ParserModel } from '../../chain-page.models';
import { CustomFormConfig } from '../custom-form/custom-form.component';

import { ConfigChangedEvent } from './advanced-editor/advanced-editor.component';

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
  @Input() isolatedParserView = false;
  @Input() parserType: string;
  @Output() removeParser = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<any>();

  areFormsReadyToRender = false;

  ngOnInit() {
    this.configForm = this.setFormFieldValues(this.configForm);
    this.metaDataForm = this.setFormFieldValues(this.metaDataForm);

    this.configForm = this.addFormFieldListeners(this.configForm);
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
            this.configForm = this.updateFormValues('config', this.configForm);
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
        if (field.path && field.path.split('.')[0] === key) {
          const value = get(this.parser, field.path + '.' + field.name);
          field.value = value || field.defaultValue || '';
        } else if (field.name === key) {
          field.value = this.parser[key];
        }
      });
    });
  }

  setFormFieldValues(fields = []) {
    return produce(fields, (draft) => {
      draft.forEach(field => {
        field.id = [
          this.parser.id,
          field.path ? [field.path, field.name].join('.') : field.name
        ].join('-');
        if (field.path) {
          const value = get(this.parser, [field.path, field.name].join('.'));
          field.value = value || field.defaultValue || '';
        } else {
          field.value = this.parser[field.name] || field.defaultValue || '';
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
            ? produce(this.parser, (draftParser) => {
                set({
                  ...draftParser
                }, [formFieldData.path, formFieldData.name].join('.'), formFieldData.value);
              })
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

  onAdvancedEditorChanged(event: ConfigChangedEvent) {
    this.parserChange.emit({ id: this.parser.id, config: event.value });
  }

  preventCollapseOnDelete(event: Event) {
    event.stopPropagation();
  }

}
