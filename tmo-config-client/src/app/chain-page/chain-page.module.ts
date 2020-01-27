import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NgZorroAntdModule, NzMessageService } from 'ng-zorro-antd';
import { MonacoEditorModule } from 'ngx-monaco-editor';

import { ChainPageComponent } from './chain-page.component';
import { ChainPageEffects } from './chain-page.effects';
import { reducer } from './chain-page.reducers';
import { ChainViewComponent } from './components/chain-view/chain-view.component';
import { LiveViewModule } from './components/live-view/live-view.module';

@NgModule({
  declarations: [ ChainPageComponent, ChainViewComponent ],
  imports: [
    NgZorroAntdModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    StoreModule.forFeature('chain-page', reducer),
    EffectsModule.forFeature([ ChainPageEffects ]),
    MonacoEditorModule,
    LiveViewModule,
  ],
  providers: [
    NzMessageService,
  ],
  exports: [ ChainPageComponent, ChainViewComponent ]
})
export class ChainPageModule { }
