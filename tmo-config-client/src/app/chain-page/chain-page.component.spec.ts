import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { IconDefinition } from '@ant-design/icons-angular';
import { EditFill } from '@ant-design/icons-angular/icons';
import { StoreModule } from '@ngrx/store';
import { Store } from '@ngrx/store';
import { NgZorroAntdModule, NZ_ICONS } from 'ng-zorro-antd';
import { Observable, of } from 'rxjs';

import * as fromActions from './chain-page.actions';
import { ChainPageComponent } from './chain-page.component';
import { ParserModel } from './chain-page.models';
import * as fromReducers from './chain-page.reducers';

const icons: IconDefinition[] = [EditFill];
@Component({
  selector: 'app-chain-view',
  template: ''
})
class MockChainViewComponent {
  @Input() parsers: ParserModel[];
  @Input() dirtyParsers;
}

@Component({
  selector: 'app-live-view',
  template: ''
})
class MockLiveViewComponent {
  @Input() chainConfig$: Observable<{}>;
}

const fakeActivatedRoute = {
  params: of({})
};

describe('ChainPageComponent', () => {
  let component: ChainPageComponent;
  let fixture: ComponentFixture<ChainPageComponent>;
  let store: Store<ChainPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        StoreModule.forRoot({
          'chain-page': fromReducers.reducer
        }),
        NoopAnimationsModule,
        ReactiveFormsModule,
      ],
      declarations: [ChainPageComponent, MockChainViewComponent, MockLiveViewComponent],
      providers: [
        { provide: ActivatedRoute, useFactory: () => fakeActivatedRoute },
        { provide: NZ_ICONS, useValue: icons }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(Store);
    fixture = TestBed.createComponent(ChainPageComponent);
    component = fixture.componentInstance;
    component.chain = {
      id: '1',
      name: 'chain',
      parsers: []
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the popconfirm textbox for updating the chain name', () => {
    fixture.detectChanges();
    const editBtn: HTMLButtonElement = fixture.nativeElement.querySelector('[data-qe-id="chain-name-edit-btn"]');
    editBtn.click();
    expect(editBtn).toBeTruthy();
    fixture.detectChanges();

    const nameField = document.querySelector('[data-qe-id="chain-name-field"]');
    expect(nameField).toBeTruthy();
  });

  it('should disable the chain name set btn if input length < 3', () => {
    const editBtn: HTMLButtonElement = fixture.nativeElement.querySelector('[data-qe-id="chain-name-edit-btn"]');
    editBtn.click();
    fixture.detectChanges();

    const nameField: HTMLInputElement = document.querySelector('[data-qe-id="chain-name-field"]');
    nameField.value = 'aa';
    nameField.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const submitBtn: HTMLButtonElement = document.querySelector('[data-qe-id="edit-chain-name-submit-btn"]');
    expect(submitBtn.disabled).toBe(true);
  });

  it('should enable the chain name set btn if input length < 3', () => {
    const editBtn: HTMLButtonElement = fixture.nativeElement.querySelector('[data-qe-id="chain-name-edit-btn"]');
    editBtn.click();
    fixture.detectChanges();

    const nameField: HTMLInputElement = document.querySelector('[data-qe-id="chain-name-field"]');
    nameField.value = 'aaa';
    nameField.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const submitBtn: HTMLButtonElement = document.querySelector('[data-qe-id="edit-chain-name-submit-btn"]');
    expect(submitBtn.disabled).toBe(false);
  });

  it('should call the onBreadcrumbEditDone()', () => {
    spyOn(component, 'onBreadcrumbEditDone');

    const editBtn: HTMLButtonElement = fixture.nativeElement.querySelector('[data-qe-id="chain-name-edit-btn"]');
    editBtn.click();
    fixture.detectChanges();

    const nameField: HTMLInputElement = document.querySelector('[data-qe-id="chain-name-field"]');
    nameField.value = 'hello';
    nameField.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const submitBtn: HTMLButtonElement = document.querySelector('[data-qe-id="edit-chain-name-submit-btn"]');
    submitBtn.click();
    fixture.detectChanges();

    expect(component.onBreadcrumbEditDone).toHaveBeenCalled();
  });

  it('onBreadcrumbEditDone() will call the UpdateChain and SetDirty Actions', () => {
    spyOn(store, 'dispatch');
    // spyOn(component, 'onBreadcrumbEditDone').and.callThrough();

    const editBtn: HTMLButtonElement = fixture.nativeElement.querySelector('[data-qe-id="chain-name-edit-btn"]');
    editBtn.click();
    fixture.detectChanges();

    const nameField: HTMLInputElement = document.querySelector('[data-qe-id="chain-name-field"]');
    nameField.value = 'new_name';
    nameField.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    // commented codes are giving console errors on run
    const submitBtn: HTMLButtonElement = document.querySelector('[data-qe-id="edit-chain-name-submit-btn"]');
    submitBtn.click();
    fixture.detectChanges();

    // using the function call instead of button click
    // component.onBreadcrumbEditDone(component.chain);
    // expect(component.onBreadcrumbEditDone).toHaveBeenCalled();

    const actionUpdate = new fromActions.UpdateChainAction({chain: {id: '1', name: 'new_name'}});
    const actionDirty = new fromActions.SetDirtyAction({dirty: true});

    expect(store.dispatch).toHaveBeenCalledWith(actionUpdate);
    expect(store.dispatch).toHaveBeenCalledWith(actionDirty);
  });

});
