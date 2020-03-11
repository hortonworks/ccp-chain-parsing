import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { CustomFormConfig } from '../../custom-form.component';

@Component({
  selector: 'app-multi-input',
  templateUrl: './multi-input.component.html',
  styleUrls: ['./multi-input.component.scss']
})
export class MultiInputComponent implements OnInit {

  @Input() config: CustomFormConfig;
  @Output() changeValue = new EventEmitter<{ [key: string]: string }[]>();

  count = 0;
  value: { [key: string]: string }[] = [];

  constructor() { }

  ngOnInit() {
    this.value = [{
      [this.config.name]: ''
    }];
  }

  onAddClick() {
    this.value.push({
      [this.config.name]: ''
    });
  }

  onChange(event: Event, index: number, config: CustomFormConfig) {
    const value = ((event.currentTarget as HTMLInputElement).value || '').trim();
    this.value[index][config.name] = value;
    this.changeValue.emit(this.value);
  }

}
