import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { TransactionComponentsPage, TransactionDeleteDialog, TransactionUpdatePage } from './transaction.page-object';

const expect = chai.expect;

describe('Transaction e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let transactionComponentsPage: TransactionComponentsPage;
  let transactionUpdatePage: TransactionUpdatePage;
  let transactionDeleteDialog: TransactionDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Transactions', async () => {
    await navBarPage.goToEntity('transaction');
    transactionComponentsPage = new TransactionComponentsPage();
    await browser.wait(ec.visibilityOf(transactionComponentsPage.title), 5000);
    expect(await transactionComponentsPage.getTitle()).to.eq('Transactions');
    await browser.wait(
      ec.or(ec.visibilityOf(transactionComponentsPage.entities), ec.visibilityOf(transactionComponentsPage.noResult)),
      1000
    );
  });

  it('should load create Transaction page', async () => {
    await transactionComponentsPage.clickOnCreateButton();
    transactionUpdatePage = new TransactionUpdatePage();
    expect(await transactionUpdatePage.getPageTitle()).to.eq('Create or edit a Transaction');
    await transactionUpdatePage.cancel();
  });

  it('should create and save Transactions', async () => {
    const nbButtonsBeforeCreate = await transactionComponentsPage.countDeleteButtons();

    await transactionComponentsPage.clickOnCreateButton();

    await promise.all([
      transactionUpdatePage.setBorrowDateInput('2000-12-31'),
      transactionUpdatePage.setReturnDateInput('2000-12-31'),
      transactionUpdatePage.bookSelectLastOption(),
      transactionUpdatePage.clientSelectLastOption(),
    ]);

    expect(await transactionUpdatePage.getBorrowDateInput()).to.eq('2000-12-31', 'Expected borrowDate value to be equals to 2000-12-31');
    expect(await transactionUpdatePage.getReturnDateInput()).to.eq('2000-12-31', 'Expected returnDate value to be equals to 2000-12-31');

    await transactionUpdatePage.save();
    expect(await transactionUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await transactionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Transaction', async () => {
    const nbButtonsBeforeDelete = await transactionComponentsPage.countDeleteButtons();
    await transactionComponentsPage.clickOnLastDeleteButton();

    transactionDeleteDialog = new TransactionDeleteDialog();
    expect(await transactionDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Transaction?');
    await transactionDeleteDialog.clickOnConfirmButton();

    expect(await transactionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
