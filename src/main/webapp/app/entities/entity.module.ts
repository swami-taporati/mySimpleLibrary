import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'author',
        loadChildren: () => import('./author/author.module').then(m => m.MySimpleLibraryAuthorModule),
      },

      {
        path: 'book',
        loadChildren: () => import('./book/book.module').then(m => m.MySimpleLibraryBookModule),
      },

      {
        path: 'client',
        loadChildren: () => import('./client/client.module').then(m => m.MySimpleLibraryClientModule),
      },

      {
        path: 'publisher',
        loadChildren: () => import('./publisher/publisher.module').then(m => m.MySimpleLibraryPublisherModule),
      },

      {
        path: 'transaction',
        loadChildren: () => import('./transaction/transaction.module').then(m => m.MySimpleLibraryTransactionModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class MySimpleLibraryEntityModule {}
