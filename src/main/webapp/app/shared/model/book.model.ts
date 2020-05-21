import { IPublisher } from 'app/shared/model/publisher.model';
import { IAuthor } from 'app/shared/model/author.model';
import { BookStatus } from 'app/shared/model/enumerations/book-status.model';

export interface IBook {
  id?: number;
  isbn?: string;
  name?: string;
  publishYear?: string;
  status?: BookStatus;
  publisher?: IPublisher;
  authors?: IAuthor[];
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public isbn?: string,
    public name?: string,
    public publishYear?: string,
    public status?: BookStatus,
    public publisher?: IPublisher,
    public authors?: IAuthor[]
  ) {}
}
