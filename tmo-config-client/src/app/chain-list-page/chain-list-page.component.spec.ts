import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IconDefinition } from '@ant-design/icons-angular';
import { DeleteFill, RightSquareFill } from '@ant-design/icons-angular/icons';
import { EffectsModule } from '@ngrx/effects';
import { Store, StoreModule } from '@ngrx/store';
import { NgZorroAntdModule, NZ_ICONS } from 'ng-zorro-antd';
import { of } from 'rxjs';

import { ChainListPageService } from './../services/chain-list-page.service';
import * as fromActions from './chain-list-page.actions';
import { ChainListPageComponent } from './chain-list-page.component';
import { ChainListEffects } from './chain-list-page.effects';
import * as fromReducers from './chain-list-page.reducers';

const icons: IconDefinition[] = [DeleteFill, RightSquareFill];

class FakeChainListPageService {
  getChains() {
    return of([{
      id: 'id1',
      name: 'Chain 1'
    }, {
      id: 'id2',
      name: 'Chain 2'
    }, {
      id: 'id3',
      name: 'Chain 3'
    }]);
  }

  deleteChain() {
    return of([]);
  }

  createChain() {
    return of({});
  }
}

describe('ChainListPageComponent', () => {
  let component: ChainListPageComponent;
  let fixture: ComponentFixture<ChainListPageComponent>;
  let service: ChainListPageService;
  let store: Store<fromReducers.ChainListPageState>;

  function clickOkOnPopConfirm() {
    (document.querySelector(
      '.ant-popover .ant-btn-primary'
    ) as HTMLElement).click();
  }

  function clickDeleteBtnOnIndex(index: number) {
    fixture.debugElement
      .queryAll(By.css('.chain-delete-btn'))
      [index].nativeElement.click();
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
        StoreModule.forRoot({
          'chain-list-page': fromReducers.reducer
        }),
        EffectsModule.forRoot([ChainListEffects])
      ],
      declarations: [ChainListPageComponent],
      providers: [
        {
          provide: ChainListPageService,
          useClass: FakeChainListPageService
        },
        { provide: NZ_ICONS, useValue: icons }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(Store);
    service = TestBed.get(ChainListPageService);
    fixture = TestBed.createComponent(ChainListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show up confimation when user click the delete button', () => {
    component.chains$ = of([
      { id: 'id1', name: 'Chain 1' },
      { id: 'id2', name: 'Chain 2' },
      { id: 'id3', name: 'Chain 3' }
    ]);
    fixture.detectChanges();

    const indexOfSecondDeleteBtn = 1;
    clickDeleteBtnOnIndex(indexOfSecondDeleteBtn);
    fixture.detectChanges();
    const popover = document.querySelector('.ant-popover');
    expect(popover).toBeTruthy();
  });

  it('should dispatch an action to delete the chain', () => {
    spyOn(store, 'dispatch').and.callThrough();
    component.chains$ = of([
      { id: 'id1', name: 'Chain 1' },
      { id: 'id2', name: 'Chain 2' },
      { id: 'id3', name: 'Chain 3' }
    ]);
    fixture.detectChanges();
    const index = 0;
    clickDeleteBtnOnIndex(index);
    fixture.detectChanges();

    clickOkOnPopConfirm();
    fixture.detectChanges();
    const action = new fromActions.DeleteChainAction('id1');

    fixture.detectChanges();
    expect(store.dispatch).toHaveBeenCalledWith(action);
  });

  it('should call the pushValue function', () => {
    const addBtn = fixture.nativeElement.querySelector('[data-qe-id="add-chain-btn"]');
    addBtn.click();
    fixture.detectChanges();

    const chainNameField: HTMLInputElement = fixture.debugElement.queryAll(By.css('[data-qe-id="chain-name"]'))[0].nativeElement;
    expect(chainNameField).toBeDefined();

    const pushMethodSpy = spyOn(component, 'pushChain');
    chainNameField.value = 'New Chain';
    chainNameField.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const createBtn = fixture.debugElement.queryAll(By.css('.ant-modal .ant-btn-primary'))[0].nativeElement;
    createBtn.click();
    fixture.detectChanges();
    expect(pushMethodSpy).toHaveBeenCalledTimes(1);
  });

  it('should call the CreateChainAction', () => {
    spyOn(store, 'dispatch').and.callThrough();

    const addBtn = fixture.nativeElement.querySelector('[data-qe-id="add-chain-btn"]');
    addBtn.click();
    fixture.detectChanges();

    const chainNameField: HTMLInputElement = fixture.debugElement.queryAll(By.css('[data-qe-id="chain-name"]'))[0].nativeElement;
    expect(chainNameField).toBeDefined();

    chainNameField.value = 'New Chain';
    chainNameField.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const createBtn = fixture.debugElement.queryAll(By.css('.ant-modal .ant-btn-primary'))[0].nativeElement;
    createBtn.click();
    fixture.detectChanges();
    const action = new fromActions.CreateChainAction({name: 'New Chain'});

    expect(store.dispatch).toHaveBeenCalledWith(action);
  });
});
