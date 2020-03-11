import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { PlusCircleFill } from '@ant-design/icons-angular/icons';
import { NgZorroAntdModule, NZ_ICONS } from 'ng-zorro-antd';

import { MultiInputComponent } from './multi-input.component';

describe('MultiInputComponent', () => {
  let component: MultiInputComponent;
  let fixture: ComponentFixture<MultiInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule
      ],
      declarations: [ MultiInputComponent ],
      providers: [
        { provide: NZ_ICONS, useValue: [PlusCircleFill] }
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

  it('should add an item to the array', () => {
    component.config = {
      type: 'text',
      name: 'foo'
    };
    expect(component.value).toEqual([{
      foo: ''
    }]);
    component.onAddClick();
    expect(component.value).toEqual([{
      foo: ''
    }, {
      foo: ''
    }]);
    component.onAddClick();
    expect(component.value).toEqual([{
      foo: ''
    }, {
      foo: ''
    }, {
      foo: ''
    }]);
  });

  it('should emit change with the proper payload', () => {
    const spy = spyOn(component.changeValue, 'emit');
    component.onChange(({
      currentTarget: {
        value: '   trim me!!    '
      }
    } as unknown) as Event, 0, {
      type: 'text',
      name: 'foo'
    });
    expect(component.value[0].foo).toBe('trim me!!');
    expect(spy).toHaveBeenCalledWith([{
      foo: 'trim me!!'
    }]);
  });
});
