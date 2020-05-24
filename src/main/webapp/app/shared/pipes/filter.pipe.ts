import { PipeTransform, Pipe } from '@angular/core';
import { IBook } from '../model/book.model';
import { BookStatus } from '../model/enumerations/book-status.model';

@Pipe({
  name: 'callback',
  pure: false,
})
export class CallbackPipe implements PipeTransform {
  transform(items: IBook[], status: BookStatus): IBook[] {
    if (!items) {
      /* eslint-disable no-console */
      console.debug('EMPTY CHECK' + items);
      /* eslint-disable no-console */
      return items;
    }
    /* eslint-disable no-console */
    console.debug('LOG CHECK' + items);
    /* eslint-enable no-console */

    return items.filter(item => (item.status as BookStatus) === status);
  }
}
