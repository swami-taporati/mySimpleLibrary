import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IPublisher, Publisher } from 'app/shared/model/publisher.model';
import { PublisherService } from './publisher.service';

@Component({
  selector: 'jhi-publisher-update',
  templateUrl: './publisher-update.component.html',
})
export class PublisherUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(100)]],
  });

  constructor(protected publisherService: PublisherService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ publisher }) => {
      this.updateForm(publisher);
    });
  }

  updateForm(publisher: IPublisher): void {
    this.editForm.patchValue({
      id: publisher.id,
      name: publisher.name,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const publisher = this.createFromForm();
    if (publisher.id !== undefined) {
      this.subscribeToSaveResponse(this.publisherService.update(publisher));
    } else {
      this.subscribeToSaveResponse(this.publisherService.create(publisher));
    }
  }

  private createFromForm(): IPublisher {
    return {
      ...new Publisher(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPublisher>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
