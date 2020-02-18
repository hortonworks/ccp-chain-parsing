import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ChainDetailsModel } from '../chain-page/chain-page.models';

@Injectable({
    providedIn: 'root'
})
export class ChainPageService {

    private readonly BASE_URL = '/api/v1/parserconfig/';

    constructor(
      private http: HttpClient
    ) {}

    public getChain(id: string) {
      return this.http.get(this.BASE_URL + `chains/${id}`);
    }

    public getParsers(id: string) {
      return this.http.get(this.BASE_URL + `chains/${id}/parsers`);
    }

    public saveParserConfig(chainId: string, config: ChainDetailsModel) {
      return this.http.put(this.BASE_URL + `chains/${chainId}`, config);
    }

    public getFormConfig(type: string) {
      return this.http.get(this.BASE_URL + `parser-form-configuration/${type}`);
    }

    public getFormConfigs() {
      return this.http.get(this.BASE_URL + `parser-form-configuration`);
    }
}
