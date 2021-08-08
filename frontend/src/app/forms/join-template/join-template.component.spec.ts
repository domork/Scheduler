import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JoinTemplateComponent } from './join-template.component';

describe('JoinTemplateComponent', () => {
  let component: JoinTemplateComponent;
  let fixture: ComponentFixture<JoinTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ JoinTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(JoinTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
