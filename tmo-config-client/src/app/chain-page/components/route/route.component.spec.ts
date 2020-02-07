import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import { provideMockStore } from '@ngrx/store/testing';

import * as fromReducers from '../../chain-page.reducers';

import { RouteComponent } from './route.component';

describe('RouteComponent', () => {
  let component: RouteComponent;
  let fixture: ComponentFixture<RouteComponent>;
  const initialState = {
    'chain-page': {
      parsers: {},
      routes: {
        123: {
          id: '123',
          name: 'some route',
          subchain: '456'
        }
      },
      chains: {
        456: {
          id: '456',
          name: 'some chain',
          parsers: []
        }
      },
      error: ''
    }
  };
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({
          'chain-page': fromReducers.reducer
        })
      ],
      declarations: [ RouteComponent ],
      providers: provideMockStore({ initialState }),
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RouteComponent);
    component = fixture.componentInstance;
    component.routeId = '123';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
