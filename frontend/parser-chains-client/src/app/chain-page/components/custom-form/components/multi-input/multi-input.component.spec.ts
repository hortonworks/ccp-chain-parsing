import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MinusCircleFill, PlusCircleFill } from '@ant-design/icons-angular/icons';
import { NgZorroAntdModule, NZ_ICONS } from 'ng-zorro-antd';

import { MultiInputComponent } from './multi-input.component';

describe('MultiInputComponent', () => {
  let component: MultiInputComponent;
  let fixture: ComponentFixture<MultiInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        ReactiveFormsModule
      ],
      declarations: [ MultiInputComponent ],
      providers: [
        { provide: NZ_ICONS, useValue: [PlusCircleFill, MinusCircleFill] }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiInputComponent);
    component = fixture.componentInstance;
    component.config = { name: 'foo', type: 'text' };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add one form control', () => {
    component.config = {
      type: 'text',
      name: 'foo'
    };
    expect(component.controls.length).toBe(1);
    expect(component.controls[0].value).toEqual('');
    component.onAddClick();
    expect(component.controls.length).toBe(2);
    expect(component.controls[0].value).toEqual('');
    expect(component.controls[1].value).toEqual('');
    component.onAddClick();
    expect(component.controls.length).toBe(3);
    expect(component.controls[0].value).toEqual('');
    expect(component.controls[1].value).toEqual('');
    expect(component.controls[2].value).toEqual('');
  });

  it('should emit change with the proper payload', () => {
    const spy = spyOn(component.changeValue, 'emit');
    component.controls = [
      new FormControl('value 1'),
      new FormControl('value 2'),
      new FormControl('value 3'),
    ];
    component.onChange({
      type: 'text',
      name: 'foo'
    });
    expect(spy).toHaveBeenCalledWith([{
      foo: 'value 1'
    }, {
      foo: 'value 2'
    }, {
      foo: 'value 3'
    }]);
  });

  it('should remove one form control', () => {
    const spy = spyOn(component.changeValue, 'emit');
    component.config = {
      type: 'text',
      name: 'foo'
    };
    expect(component.controls.length).toBe(1);

    component.onAddClick();
    component.onAddClick();

    expect(component.controls.length).toBe(3);

    const controlToBeRemoved = component.controls[0];
    component.onRemoveFieldClick(controlToBeRemoved, component.config);

    expect(component.controls.length).toBe(2);

    expect(component.controls.find((c) => c === controlToBeRemoved))
      .toBeUndefined();

    expect(spy).toHaveBeenCalledWith([{
      foo: ''
    }, {
      foo: ''
    }]);
  });
});
