import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class ChainPageService {

    private readonly BASE_URL = '/api/v1/parserconfig/';

    constructor(
      private http: HttpClient
    ) {}

    public getParsers(id: string) {
      return this.http.get(this.BASE_URL + `chains/${id}/parsers`);
    }

}
