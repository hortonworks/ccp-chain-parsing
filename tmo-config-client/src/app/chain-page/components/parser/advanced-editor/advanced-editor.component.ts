import { Component, Input, Output, EventEmitter } from '@angular/core';

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
    this.configChanged.emit({ value: JSON.parse(value) });
  }

}
