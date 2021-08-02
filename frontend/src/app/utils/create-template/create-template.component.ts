import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {CreateGroupForm} from "../dto/create-group-form";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-create-template',
  templateUrl: './create-template.component.html',
  styleUrls: ['./create-template.component.scss']
})
export class CreateTemplateComponent implements OnInit {
  @Output() formSubmit: EventEmitter<CreateGroupForm> = new EventEmitter<CreateGroupForm>();

  constructor() { }

  ngOnInit(): void {
  }

  onSubmit(form: NgForm): void {
    this.formSubmit.emit(form.value);
    form.reset();
  }
}
