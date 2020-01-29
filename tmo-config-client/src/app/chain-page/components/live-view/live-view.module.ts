import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzRadioModule } from 'ng-zorro-antd/radio';
import { NzTabsModule } from 'ng-zorro-antd/tabs';
import { NzSpinModule } from 'ng-zorro-antd/spin';

import { LiveViewComponent } from './live-view.component';
import { LiveViewEffects } from './live-view.effects';
import { reducer } from './live-view.reducers';
import { SampleDataFormComponent } from './sample-data-form/sample-data-form.component';
import { LiveViewService } from './services/live-view.service';

@NgModule({
  declarations: [ LiveViewComponent, SampleDataFormComponent ],
  imports: [
    CommonModule,
    FormsModule,
    StoreModule.forFeature('live-view', reducer),
    EffectsModule.forFeature([ LiveViewEffects ]),
    NzTabsModule,
    NzFormModule,
    NzButtonModule,
    NzRadioModule,
    NzInputModule,
    NzSpinModule,
  ],
  providers: [
    LiveViewService
  ],
  exports: [ LiveViewComponent ]
})
export class LiveViewModule { }
