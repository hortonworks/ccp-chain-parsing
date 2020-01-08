import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MainContainerComponent } from './misc/main/main.container';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

export const routes: Routes = [
  { path: '404', component: PageNotFoundComponent },
  { path: '', component: MainContainerComponent },
  { path: ':type', component: MainContainerComponent },
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

