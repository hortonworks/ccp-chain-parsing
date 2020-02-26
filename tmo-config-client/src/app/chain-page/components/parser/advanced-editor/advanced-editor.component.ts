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
    const jsonRepresentation = JSON.parse(value);
    if (this.isConfigEqual(jsonRepresentation, this.config)) return;

    this.configChanged.emit({ value: jsonRepresentation });
  }

  private isConfigEqual(val1: {}, val2: {}) {
    const ok = Object.keys;
    return ok(val1).every(val1key => ok(val2).some(val2key => val1key === val2key)) &&
      ok(val2).every(val2key => ok(val1).some(val1key => val1key === val2key)) &&
      ok(val1).every(val1key => ok(val2).some(val2key => val2[val2key] === val1[val1key]));
  }

}
