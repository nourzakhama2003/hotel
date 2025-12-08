import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomcardComponent } from './room-card.component';

describe('RoomcardComponent', () => {
  let component: RoomcardComponent;
  let fixture: ComponentFixture<RoomcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomcardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
