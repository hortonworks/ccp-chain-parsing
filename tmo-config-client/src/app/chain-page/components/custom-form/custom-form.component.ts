import { Component, Input, OnInit } from '@angular/core';

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
export class CustomFormComponent implements OnInit {

  @Input() config: CustomFormConfig[] = [];

  constructor() { }

  ngOnInit() {
  }

  onChange(event: Event, config: CustomFormConfig) {
    if (typeof config.onChange === 'function') {
      config.onChange({
        ...config,
        value: (event.currentTarget as HTMLFormElement).value
      });
    }
  }

}
