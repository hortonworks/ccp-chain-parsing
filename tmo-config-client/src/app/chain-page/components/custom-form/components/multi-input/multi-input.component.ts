import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';

import { CustomFormConfig } from '../../custom-form.component';

@Component({
  selector: 'app-multi-input',
  templateUrl: './multi-input.component.html',
  styleUrls: ['./multi-input.component.scss']
})
export class MultiInputComponent implements OnInit {

  @Input() config: CustomFormConfig;
  @Input() value: { [key: string]: string }[] = [];
  @Output() changeValue = new EventEmitter<{ [key: string]: string }[]>();

  count = 0;
  controls = [];

  constructor() { }

  ngOnInit() {
    if (!Array.isArray(this.value) || this.value.length === 0) {
      this.controls.push(
        new FormControl('')
      );
    } else {
      this.controls = this.value.map(item => {
        return new FormControl(item[this.config.name]);
      });
    }
  }

  onAddClick() {
    this.controls.push(
      new FormControl('')
    );
  }

  onChange(config: CustomFormConfig) {
    const value = this.controls.map(control => {
      return {
        [config.name]: control.value
      };
    });
    this.changeValue.emit(value);
  }
}
