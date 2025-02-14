import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DeleteFill } from '@ant-design/icons-angular/icons';
import { NgZorroAntdModule, NZ_ICONS } from 'ng-zorro-antd';

import { RouterComponent } from './router.component';

@Component({
  selector: 'app-custom-form',
  template: '',
})
export class MockCustomFormComponent {
  @Input() config = [];
}

@Component({
  selector: 'app-route',
  template: ''
})
export class MockRouteComponent {
  @Input() routeId;
  @Input() parser;
}

describe('RouterComponent', () => {
  let component: RouterComponent;
  let fixture: ComponentFixture<RouterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgZorroAntdModule,
        NoopAnimationsModule,
        FormsModule,
      ],
      declarations: [
        MockRouteComponent,
        MockCustomFormComponent,
        RouterComponent
      ],
      providers: [
        {provide: NZ_ICONS, useValue: [DeleteFill]}
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RouterComponent);
    component = fixture.componentInstance;
    component.parser = {
      id: '123',
      name: 'Some parser',
      type: 'Bro'
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit a parser change', () => {
    const spy = spyOn(component.parserChange, 'emit');
    component.onMatchingFieldBlur(({
      target: {
        value: ' trim me!     '
      }
    } as unknown) as Event, {
      id: '123',
      name: 'parser',
      type: 'foo',
      routing: {
        lorem: 'ipsum'
      }
    });
    expect(spy).toHaveBeenCalledWith({
      id: '123',
      routing: {
        lorem: 'ipsum',
        matchingField: 'trim me!'
      }
    });
  });

  it('should emit subchain select', () => {
    const spy = spyOn(component.subchainSelect, 'emit');
    component.onSubchainClick('777');
    expect(spy).toHaveBeenCalledWith('777');
  });

  it('should emit route add', () => {
    const spy = spyOn(component.routeAdd, 'emit');
    const event = ({
      preventDefault: jasmine.createSpy()
    } as unknown) as Event;
    component.onAddRouteClick(event, {
      id: '123',
      name: 'parser',
      type: 'lorem'
    });
    expect(event.preventDefault).toHaveBeenCalled();
    expect(spy).toHaveBeenCalledWith({
      id: '123',
      name: 'parser',
      type: 'lorem'
    });
  });

  it('should have a track function', () => {
    expect(component.trackByFn(0, '444')).toBe('444');
  });
});
