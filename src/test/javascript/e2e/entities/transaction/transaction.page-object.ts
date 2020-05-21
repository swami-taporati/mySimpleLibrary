import { element, by, ElementFinder } from 'protractor';

export class TransactionComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-transaction div table .btn-danger'));
  title = element.all(by.css('jhi-transaction div h2#page-heading span')).first();
  noResult = element(by.id('no-result'));
  entities = element(by.id('entities'));

  async clickOnCreateButton(): Promise<void> {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(): Promise<void> {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons(): Promise<number> {
    return this.deleteButtons.count();
  }

  async getTitle(): Promise<string> {
    return this.title.getText();
  }
}

export class TransactionUpdatePage {
  pageTitle = element(by.id('jhi-transaction-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  borrowDateInput = element(by.id('field_borrowDate'));
  returnDateInput = element(by.id('field_returnDate'));

  bookSelect = element(by.id('field_book'));
  clientSelect = element(by.id('field_client'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
  }

  async setBorrowDateInput(borrowDate: string): Promise<void> {
    await this.borrowDateInput.sendKeys(borrowDate);
  }

  async getBorrowDateInput(): Promise<string> {
    return await this.borrowDateInput.getAttribute('value');
  }

  async setReturnDateInput(returnDate: string): Promise<void> {
    await this.returnDateInput.sendKeys(returnDate);
  }

  async getReturnDateInput(): Promise<string> {
    return await this.returnDateInput.getAttribute('value');
  }

  async bookSelectLastOption(): Promise<void> {
    await this.bookSelect.all(by.tagName('option')).last().click();
  }

  async bookSelectOption(option: string): Promise<void> {
    await this.bookSelect.sendKeys(option);
  }

  getBookSelect(): ElementFinder {
    return this.bookSelect;
  }

  async getBookSelectedOption(): Promise<string> {
    return await this.bookSelect.element(by.css('option:checked')).getText();
  }

  async clientSelectLastOption(): Promise<void> {
    await this.clientSelect.all(by.tagName('option')).last().click();
  }

  async clientSelectOption(option: string): Promise<void> {
    await this.clientSelect.sendKeys(option);
  }

  getClientSelect(): ElementFinder {
    return this.clientSelect;
  }

  async getClientSelectedOption(): Promise<string> {
    return await this.clientSelect.element(by.css('option:checked')).getText();
  }

  async save(): Promise<void> {
    await this.saveButton.click();
  }

  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class TransactionDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-transaction-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-transaction'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
