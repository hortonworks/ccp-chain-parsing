import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

export interface CustomFormConfig {
  id: string;
  name: string;
  type: string;
  value?: string;
  onChange?: (config) => {};
}

@Component({
  selector: 'app-custom-form',
  templateUrl: './custom-form.component.html',
  styleUrls: ['./custom-form.component.scss']
})
export class CustomFormComponent implements OnInit, OnChanges {

  @Input() config: CustomFormConfig[] = [];

  formGroup: FormGroup;

  constructor() { }

  ngOnInit() {
    this.formGroup = new FormGroup(this.config.reduce((controls, fieldConfig) => {
      controls[fieldConfig.name] = new FormControl(fieldConfig.value);
      return controls;
    }, {}));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.config && changes.config.previousValue) {
      changes.config.previousValue.forEach((fieldConfig, i) => {
        this.formGroup.patchValue({
          [fieldConfig.name]: changes.config.currentValue[i].value
        });
      });
    }
  }

  onChange(event: Event, config: CustomFormConfig) {
    if (typeof config.onChange === 'function') {
      config.onChange({
        ...config,
        value: (event.currentTarget as HTMLFormElement).value
      });
    }
  }

  trackByFn(index) {
    return index;
  }
}
