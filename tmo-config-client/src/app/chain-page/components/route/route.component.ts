import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { select, Store } from '@ngrx/store';

import * as fromActions from '../../chain-page.actions';
import { ParserChainModel, ParserModel, RouteModel } from '../../chain-page.models';
import { ChainPageState, getChain, getRoute } from '../../chain-page.reducers';

@Component({
  selector: 'app-route',
  templateUrl: './route.component.html',
  styleUrls: ['./route.component.scss']
})
export class RouteComponent implements OnInit {

  @Input() routeId: string;
  @Input() parser: ParserModel;
  @Output() chainClick = new EventEmitter<string>();

  subchain: ParserChainModel;
  route: RouteModel;

  constructor(
    private store: Store<ChainPageState>,
  ) { }

  ngOnInit() {
    this.store.pipe(select(getRoute, {
      id: this.routeId
    })).subscribe((route) => {
      this.route = route;
      this.store.pipe(select(getChain, {
        id: this.route.subchain
      })).subscribe((subchain) => {
        this.subchain = subchain;
      });
    });
  }

  onChainClick(event: Event, chainId: string) {
    this.chainClick.emit(chainId);
  }

  onMatchingValueBlur(event: Event, route: RouteModel) {
    const matchingValue = ((event.target as HTMLInputElement).value || '').trim();
    if (matchingValue !== route.matchingValue) {
      this.store.dispatch(
        new fromActions.UpdateChainAction({
          chain: {
            id: this.subchain.id,
            name: matchingValue
          }
        })
      );
      this.store.dispatch(
        new fromActions.UpdateRouteAction({
          chainId: this.subchain.id,
          parserId: this.parser.id,
          route: {
            id: route.id,
            matchingValue
          }
        })
      );
    }
  }

  onRouteRemoveClick(event: Event, route: RouteModel) {
    this.store.dispatch(
      new fromActions.RemoveRouteAction({
        chainId: this.subchain.id,
        parserId: this.parser.id,
        routeId: route.id
      })
    );
  }
}
