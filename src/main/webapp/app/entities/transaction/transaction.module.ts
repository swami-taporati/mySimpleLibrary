import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MySimpleLibrarySharedModule } from 'app/shared/shared.module';
import { TransactionComponent } from './transaction.component';
import { TransactionDetailComponent } from './transaction-detail.component';
import { TransactionUpdateComponent } from './transaction-update.component';
import { TransactionDeleteDialogComponent } from './transaction-delete-dialog.component';
import { transactionRoute } from './transaction.route';
import { CallbackPipe } from 'app/shared/pipes/filter.pipe';

@NgModule({
  imports: [MySimpleLibrarySharedModule, RouterModule.forChild(transactionRoute)],
  declarations: [
    TransactionComponent,
    TransactionDetailComponent,
    TransactionUpdateComponent,
    TransactionDeleteDialogComponent,
    CallbackPipe,
  ],
  entryComponents: [TransactionDeleteDialogComponent],
})
export class MySimpleLibraryTransactionModule {}
