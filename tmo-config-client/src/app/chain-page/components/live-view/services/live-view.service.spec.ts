import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideMockStore, MockStore } from '@ngrx/store/testing';

import { LiveViewService } from './live-view.service';

describe('LiveViewService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
    providers: [
      provideMockStore({}),
    ],
  }));

  it('should be created', () => {
    const service: LiveViewService = TestBed.get(LiveViewService);
    expect(service).toBeTruthy();
  });
});
