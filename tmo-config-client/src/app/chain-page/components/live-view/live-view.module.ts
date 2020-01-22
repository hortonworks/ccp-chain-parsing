import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { LiveViewComponent } from './live-view.component';

@NgModule({
  declarations: [ LiveViewComponent ],
  imports: [
    CommonModule
  ],
  exports: [ LiveViewComponent ]
})
export class LiveViewModule { }
