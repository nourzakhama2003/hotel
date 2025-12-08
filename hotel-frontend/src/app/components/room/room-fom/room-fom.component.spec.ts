import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomFomComponent } from './room-fom.component';

describe('RoomFomComponent', () => {
  let component: RoomFomComponent;
  let fixture: ComponentFixture<RoomFomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomFomComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomFomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
