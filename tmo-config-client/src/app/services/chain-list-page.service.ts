import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import querystring from 'querystring';

import { ChainOperationalModel } from './../chain-list-page/chain.model';

@Injectable({
    providedIn: 'root'
})
export class ChainListPageService {

    private readonly BASE_URL = '/api/v1/parserconfig/';

    constructor(
      private http: HttpClient
    ) {}

    public createChain(chain: ChainOperationalModel) {
        return this.http.post('/api/v1/parserconfig/chains', chain);
    }



    public getChains(params = null) {
        let query = '';
        if (params) {
            query = `?${querystring.stringify(params)}`;
        }
        return this.http.get(`${this.BASE_URL}chains${query}`);
    }

    public deleteChain(chainId: string) {
        return this.http.delete(this.BASE_URL + 'chains/' + chainId);
    }

}
