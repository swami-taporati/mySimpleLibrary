import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IClient, Client } from 'app/shared/model/client.model';
import { ClientService } from './client.service';

@Component({
  selector: 'jhi-client-update',
  templateUrl: './client-update.component.html',
})
export class ClientUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    firstName: [null, [Validators.required, Validators.maxLength(50)]],
    lastName: [null, [Validators.required, Validators.maxLength(50)]],
    email: [null, [Validators.maxLength(50)]],
    phone: [null, [Validators.maxLength(20)]],
  });

  constructor(protected clientService: ClientService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ client }) => {
      this.updateForm(client);
    });
  }

  updateForm(client: IClient): void {
    this.editForm.patchValue({
      id: client.id,
      firstName: client.firstName,
      lastName: client.lastName,
      email: client.email,
      phone: client.phone,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const client = this.createFromForm();
    if (client.id !== undefined) {
      this.subscribeToSaveResponse(this.clientService.update(client));
    } else {
      this.subscribeToSaveResponse(this.clientService.create(client));
    }
  }

  private createFromForm(): IClient {
    return {
      ...new Client(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      phone: this.editForm.get(['phone'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClient>>): void {
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
