import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgZorroAntdModule } from 'ng-zorro-antd';

import { ChainAddParserPageComponent } from './chain-add-parser-page.component';



@NgModule({
  declarations: [ChainAddParserPageComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    NgZorroAntdModule,
  ]
})
export class ChainAddParserPageModule { }
