import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';

import { LiveViewState } from '../live-view.reducers';

@Injectable({
  providedIn: 'root'
})
export class LiveViewService {

  constructor(private store: Store<LiveViewState>) {

  }
}
