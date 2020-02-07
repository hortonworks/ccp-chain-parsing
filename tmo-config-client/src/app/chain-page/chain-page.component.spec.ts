import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { IconDefinition } from '@ant-design/icons-angular';
import { EditFill } from '@ant-design/icons-angular/icons';
import { StoreModule } from '@ngrx/store';
import { NgZorroAntdModule, NZ_ICONS } from 'ng-zorro-antd';
import { Observable, of } from 'rxjs';

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

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        StoreModule.forRoot({
          'chain-page': fromReducers.reducer
        })
      ],
      declarations: [ChainPageComponent, MockChainViewComponent, MockLiveViewComponent],
      providers: [
        { provide: ActivatedRoute, useFactory: () => fakeActivatedRoute },
        { provide: NZ_ICONS, useValue: icons }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
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

  it('should change display the textbox for chain name update', () => {
    component.editMode = false;
    fixture.detectChanges();
    const editBtn: HTMLButtonElement = fixture.nativeElement.querySelector('[data-qe-id="chain-name-edit-btn"]');
    editBtn.click();
    fixture.detectChanges();
    const nameField = fixture.nativeElement.querySelector('[data-qe-id="chain-name-field"]');
    expect(nameField).toBeTruthy();
    expect(component.editMode).toBe(true);
  });

  it('should call the updateChainName()', () => {
    spyOn(component, 'updateChainName').and.callThrough();
    spyOn(component, 'toggleEditMode').and.callThrough();
    component.editMode = false;
    fixture.detectChanges();
    const editBtn: HTMLButtonElement = fixture.nativeElement.querySelector('[data-qe-id="chain-name-edit-btn"]');
    editBtn.click();
    fixture.detectChanges();

    const nameField = fixture.nativeElement.querySelector('[data-qe-id="chain-name-field"]');
    nameField.value = 'hello';
    nameField.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    nameField.dispatchEvent(new Event('blur'));
    expect(component.updateChainName).toHaveBeenCalled();
    expect(component.toggleEditMode).toHaveBeenCalled();
  });
});
