import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzRadioModule } from 'ng-zorro-antd/radio';
import { NzTabsModule } from 'ng-zorro-antd/tabs';

import { LiveViewComponent } from './live-view.component';
import { SampleDataFormComponent } from './sample-data-form/sample-data-form.component';

@NgModule({
  declarations: [ LiveViewComponent, SampleDataFormComponent ],
  imports: [
    CommonModule,
    NzTabsModule,
    NzFormModule,
    NzButtonModule,
    NzRadioModule,
  ],
  exports: [ LiveViewComponent ]
})
export class LiveViewModule { }
