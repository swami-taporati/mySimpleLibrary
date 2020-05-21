import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IBook, Book } from 'app/shared/model/book.model';
import { BookService } from './book.service';
import { IPublisher } from 'app/shared/model/publisher.model';
import { PublisherService } from 'app/entities/publisher/publisher.service';
import { IAuthor } from 'app/shared/model/author.model';
import { AuthorService } from 'app/entities/author/author.service';

type SelectableEntity = IPublisher | IAuthor;

@Component({
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html',
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;
  publishers: IPublisher[] = [];
  authors: IAuthor[] = [];

  editForm = this.fb.group({
    id: [],
    isbn: [null, [Validators.required, Validators.minLength(5), Validators.maxLength(13)]],
    name: [null, [Validators.required, Validators.maxLength(100)]],
    publishYear: [null, [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
    status: [],
    publisher: [],
    authors: [],
  });

  constructor(
    protected bookService: BookService,
    protected publisherService: PublisherService,
    protected authorService: AuthorService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      this.updateForm(book);

      this.publisherService
        .query({ 'bookId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<IPublisher[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IPublisher[]) => {
          if (!book.publisher || !book.publisher.id) {
            this.publishers = resBody;
          } else {
            this.publisherService
              .find(book.publisher.id)
              .pipe(
                map((subRes: HttpResponse<IPublisher>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IPublisher[]) => (this.publishers = concatRes));
          }
        });

      this.authorService.query().subscribe((res: HttpResponse<IAuthor[]>) => (this.authors = res.body || []));
    });
  }

  updateForm(book: IBook): void {
    this.editForm.patchValue({
      id: book.id,
      isbn: book.isbn,
      name: book.name,
      publishYear: book.publishYear,
      status: book.status,
      publisher: book.publisher,
      authors: book.authors,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.createFromForm();
    if (book.id !== undefined) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  private createFromForm(): IBook {
    return {
      ...new Book(),
      id: this.editForm.get(['id'])!.value,
      isbn: this.editForm.get(['isbn'])!.value,
      name: this.editForm.get(['name'])!.value,
      publishYear: this.editForm.get(['publishYear'])!.value,
      status: this.editForm.get(['status'])!.value,
      publisher: this.editForm.get(['publisher'])!.value,
      authors: this.editForm.get(['authors'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
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

  getSelected(selectedVals: IAuthor[], option: IAuthor): IAuthor {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
