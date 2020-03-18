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

  @Input() collapsed: boolean;
  @Input() dirty = false;
  @Input() parser: ParserModel;
  @Input() metaDataForm: CustomFormConfig[];
  @Input() configForm: CustomFormConfig[];
  @Input() isolatedParserView = false;
  @Input() parserType: string;
  @Input() failedParser: string;
  @Output() removeParser = new EventEmitter<string>();
  @Output() parserChange = new EventEmitter<any>();

  editName = false;

  areFormsReadyToRender = false;
  parserCollapseState = [];

  get parsingFailed() {
    return this.failedParser === this.parser.id;
  }

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
          let value;
          if (field.multiple !== true) {
            value = get(this.parser, field.path + '.' + field.name);
          } else {
            value = get(this.parser, field.path);
            value = Array.isArray(value) ? value.map((item) => {
              return { [field.name]: item[field.name] || '' };
            }) : [];
          }
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
          let value;
          if (field.multiple !== true) {
            value = get(this.parser, [field.path, field.name].join('.'));
          } else {
            value = get(this.parser, field.path);
            value = Array.isArray(value) ? value.map((item) => {
              return { [field.name]: item[field.name] || '' };
            }) : [];
          }
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
          let partialParser;
          if (formFieldData.multiple !== true) {
            partialParser = formFieldData.path
            ? produce(this.parser, (draftParser) => {
                set({
                  ...draftParser
                }, [formFieldData.path, formFieldData.name].join('.'), formFieldData.value);
              })
            : {
              id: this.parser.id,
              [formFieldData.name]: formFieldData.value
            };
          } else {
            let current;
            if (formFieldData.path) {
              current = get(this.parser, formFieldData.path);
            } else {
              current = this.parser[formFieldData.name];
            }
            if (!current) {
              partialParser = produce(this.parser, (draftParser) => {
                if (formFieldData.path) {
                  set({
                    ...draftParser
                  }, formFieldData.path, formFieldData.value);
                } else {
                  draftParser[formFieldData.name] = formFieldData.value;
                }
              });
            } else {
              current = formFieldData.value.map((item, i) => {
                return {
                  ...current[i],
                  ...item
                };
              });
              partialParser = produce(this.parser, (draftParser) => {
                set({
                  ...draftParser
                }, formFieldData.path, current);
              });
            }
          }

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

  onParserNameChange(name: string) {
    this.parserChange.emit({ id: this.parser.id, name });
  }

  preventCollapse(event: Event) {
    event.stopPropagation();
  }

}
