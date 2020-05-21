import { element, by, ElementFinder } from 'protractor';

export class BookComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-book div table .btn-danger'));
  title = element.all(by.css('jhi-book div h2#page-heading span')).first();
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

export class BookUpdatePage {
  pageTitle = element(by.id('jhi-book-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  isbnInput = element(by.id('field_isbn'));
  nameInput = element(by.id('field_name'));
  publishYearInput = element(by.id('field_publishYear'));
  statusSelect = element(by.id('field_status'));

  publisherSelect = element(by.id('field_publisher'));
  authorSelect = element(by.id('field_author'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
  }

  async setIsbnInput(isbn: string): Promise<void> {
    await this.isbnInput.sendKeys(isbn);
  }

  async getIsbnInput(): Promise<string> {
    return await this.isbnInput.getAttribute('value');
  }

  async setNameInput(name: string): Promise<void> {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput(): Promise<string> {
    return await this.nameInput.getAttribute('value');
  }

  async setPublishYearInput(publishYear: string): Promise<void> {
    await this.publishYearInput.sendKeys(publishYear);
  }

  async getPublishYearInput(): Promise<string> {
    return await this.publishYearInput.getAttribute('value');
  }

  async setStatusSelect(status: string): Promise<void> {
    await this.statusSelect.sendKeys(status);
  }

  async getStatusSelect(): Promise<string> {
    return await this.statusSelect.element(by.css('option:checked')).getText();
  }

  async statusSelectLastOption(): Promise<void> {
    await this.statusSelect.all(by.tagName('option')).last().click();
  }

  async publisherSelectLastOption(): Promise<void> {
    await this.publisherSelect.all(by.tagName('option')).last().click();
  }

  async publisherSelectOption(option: string): Promise<void> {
    await this.publisherSelect.sendKeys(option);
  }

  getPublisherSelect(): ElementFinder {
    return this.publisherSelect;
  }

  async getPublisherSelectedOption(): Promise<string> {
    return await this.publisherSelect.element(by.css('option:checked')).getText();
  }

  async authorSelectLastOption(): Promise<void> {
    await this.authorSelect.all(by.tagName('option')).last().click();
  }

  async authorSelectOption(option: string): Promise<void> {
    await this.authorSelect.sendKeys(option);
  }

  getAuthorSelect(): ElementFinder {
    return this.authorSelect;
  }

  async getAuthorSelectedOption(): Promise<string> {
    return await this.authorSelect.element(by.css('option:checked')).getText();
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

export class BookDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-book-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-book'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
