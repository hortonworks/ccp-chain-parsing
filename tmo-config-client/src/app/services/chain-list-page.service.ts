import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ChainOperationModel } from './../chain-list-page/chain.model';

@Injectable({
    providedIn: 'root'
})
export class ChainListPageService {
    constructor(
      private http: HttpClient
    ) {}

    public createEnrichmentType(chain: ChainOperationModel) {
        return this.http.post('api/v1/chains/', chain);
    }
}
