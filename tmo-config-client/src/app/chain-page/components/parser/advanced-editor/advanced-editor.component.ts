import { Component, Input, Output, EventEmitter } from '@angular/core';
import isEqual from 'lodash.isequal';

export interface ConfigChangedEvent {
  value: {};
}

@Component({
  selector: 'app-advanced-editor',
  templateUrl: './advanced-editor.component.html',
  styleUrls: ['./advanced-editor.component.scss']
})
export class AdvancedEditorComponent {

  @Input() config = {};
  @Output('onConfigChanged') configChanged = new EventEmitter<ConfigChangedEvent>();

  monacoOptions = {
    language: 'json',
    glyphMargin: false,
    folding: false,
    lineDecorationsWidth: 10,
    lineNumbersMinChars: 0,
    minimap: {
      enabled: false
    },
    automaticLayout: true,
    formatOnPaste: true,
  }

  onChange(value: string) {
    let json = {};

    try {
      json = JSON.parse(value);
    } catch {
      return;
    }

    if (isEqual(json, this.config)) return;

    this.configChanged.emit({ value: json });
  }

}
