import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ChainModel } from './../chain-list-page/chain.model';

@Injectable({
    providedIn: 'root'
})
export class ChainListPageService {
    constructor(
      private http: HttpClient
    ) {}

    public createChain(chain: ChainModel) {
        return this.http.post('api/v1/parserconfig/chains', chain);
    }
}
