import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NzCardModule } from 'ng-zorro-antd';

import { LiveViewResultComponent } from './live-view-result.component';

describe('LiveViewResultComponent', () => {
  let component: LiveViewResultComponent;
  let fixture: ComponentFixture<LiveViewResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LiveViewResultComponent ],
      imports: [ NzCardModule ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LiveViewResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
