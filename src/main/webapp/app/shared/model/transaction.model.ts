import { Moment } from 'moment';
import { IBook } from 'app/shared/model/book.model';
import { IClient } from 'app/shared/model/client.model';

export interface ITransaction {
  id?: number;
  borrowDate?: Moment;
  returnDate?: Moment;
  book?: IBook;
  client?: IClient;
}

export class Transaction implements ITransaction {
  constructor(public id?: number, public borrowDate?: Moment, public returnDate?: Moment, public book?: IBook, public client?: IClient) {}
}
