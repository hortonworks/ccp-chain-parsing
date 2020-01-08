import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import en from '@angular/common/locales/en';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { MetaReducer, StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { en_US, NgZorroAntdModule, NZ_I18N, NzLayoutModule } from 'ng-zorro-antd';
import { storeFreeze } from 'ngrx-store-freeze';
import { MonacoEditorModule } from 'ngx-monaco-editor';

import { environment } from '../environments/environment';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CanDeactivateComponent } from './misc/can-deactivate-component';
import { MainContainerComponent } from './misc/main/main.container';
import { ThemeSwitchModule } from './misc/theme-switch/theme-switch.module';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { MonacoEditorService } from './services/monaco-editor.service';

registerLocaleData(en);

export const metaReducers: MetaReducer<{}>[] = !environment.production
  ? [storeFreeze]
  : [];

@NgModule({
  declarations: [
    AppComponent,
    MainContainerComponent,
    PageNotFoundComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgZorroAntdModule,
    NzLayoutModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    StoreModule.forRoot({}, { metaReducers }),
    StoreDevtoolsModule.instrument(),
    EffectsModule.forRoot([]),
    ThemeSwitchModule,
    MonacoEditorModule.forRoot({
      onMonacoLoad() {
        monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
          validate: true,
          schemas: []
        });
      }
    }),
  ],
  providers: [
    { provide: NZ_I18N, useValue: en_US },
    CanDeactivateComponent,
    MonacoEditorService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
