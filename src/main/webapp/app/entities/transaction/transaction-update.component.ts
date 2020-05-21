import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ITransaction, Transaction } from 'app/shared/model/transaction.model';
import { TransactionService } from './transaction.service';
import { IBook } from 'app/shared/model/book.model';
import { BookService } from 'app/entities/book/book.service';
import { IClient } from 'app/shared/model/client.model';
import { ClientService } from 'app/entities/client/client.service';

type SelectableEntity = IBook | IClient;

@Component({
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html',
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;
  books: IBook[] = [];
  clients: IClient[] = [];
  borrowDateDp: any;
  returnDateDp: any;

  editForm = this.fb.group({
    id: [],
    borrowDate: [],
    returnDate: [],
    book: [],
    client: [],
  });

  constructor(
    protected transactionService: TransactionService,
    protected bookService: BookService,
    protected clientService: ClientService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      this.updateForm(transaction);

      this.bookService
        .query({ 'transactionId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<IBook[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IBook[]) => {
          if (!transaction.book || !transaction.book.id) {
            this.books = resBody;
          } else {
            this.bookService
              .find(transaction.book.id)
              .pipe(
                map((subRes: HttpResponse<IBook>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IBook[]) => (this.books = concatRes));
          }
        });

      this.clientService
        .query({ 'transactionId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<IClient[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IClient[]) => {
          if (!transaction.client || !transaction.client.id) {
            this.clients = resBody;
          } else {
            this.clientService
              .find(transaction.client.id)
              .pipe(
                map((subRes: HttpResponse<IClient>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IClient[]) => (this.clients = concatRes));
          }
        });
    });
  }

  updateForm(transaction: ITransaction): void {
    this.editForm.patchValue({
      id: transaction.id,
      borrowDate: transaction.borrowDate,
      returnDate: transaction.returnDate,
      book: transaction.book,
      client: transaction.client,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transaction = this.createFromForm();
    if (transaction.id !== undefined) {
      this.subscribeToSaveResponse(this.transactionService.update(transaction));
    } else {
      this.subscribeToSaveResponse(this.transactionService.create(transaction));
    }
  }

  private createFromForm(): ITransaction {
    return {
      ...new Transaction(),
      id: this.editForm.get(['id'])!.value,
      borrowDate: this.editForm.get(['borrowDate'])!.value,
      returnDate: this.editForm.get(['returnDate'])!.value,
      book: this.editForm.get(['book'])!.value,
      client: this.editForm.get(['client'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
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

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
