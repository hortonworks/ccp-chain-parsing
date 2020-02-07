import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { ParserChainModel, RouteModel } from '../../chain-page.models';
import { ChainPageState, getChain, getRoute } from '../../chain-page.reducers';

@Component({
  selector: 'app-route',
  templateUrl: './route.component.html',
  styleUrls: ['./route.component.scss']
})
export class RouteComponent implements OnInit {

  @Input() routeId: string;
  @Output() chainClick = new EventEmitter<string>();

  subchain: ParserChainModel;
  route: RouteModel;

  constructor(
    private store: Store<ChainPageState>
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
    event.preventDefault();
    this.chainClick.emit(chainId);
  }
}
