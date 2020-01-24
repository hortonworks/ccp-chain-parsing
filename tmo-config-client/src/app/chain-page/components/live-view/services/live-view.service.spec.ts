import { TestBed } from '@angular/core/testing';

import { LiveViewService } from './live-view.service';

describe('LiveViewService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LiveViewService = TestBed.get(LiveViewService);
    expect(service).toBeTruthy();
  });
});
