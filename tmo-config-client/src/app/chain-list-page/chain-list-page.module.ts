import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NgZorroAntdModule, NzInputModule, NzMessageService } from 'ng-zorro-antd';
import { MonacoEditorModule } from 'ngx-monaco-editor';

import { ChainListEffects } from './chain-list-page.effects';
import { reducer } from './chain-list-page.reducers';
import { ChainListPageComponent } from './chain-list-page.component';

@NgModule({
  declarations: [ ChainListPageComponent ],
  imports: [
    NgZorroAntdModule,
    CommonModule,
    FormsModule,
    NzInputModule,
    RouterModule,
    StoreModule.forFeature('enrichment-types', reducer),
    EffectsModule.forFeature([ ChainListEffects ]),
    MonacoEditorModule,
  ],
  providers: [
    NzMessageService,
  ],
  exports: [ ChainListPageComponent ]
})
export class ChainListPageModule { }
