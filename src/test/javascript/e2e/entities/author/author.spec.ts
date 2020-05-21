import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { AuthorComponentsPage, AuthorDeleteDialog, AuthorUpdatePage } from './author.page-object';

const expect = chai.expect;

describe('Author e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let authorComponentsPage: AuthorComponentsPage;
  let authorUpdatePage: AuthorUpdatePage;
  let authorDeleteDialog: AuthorDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Authors', async () => {
    await navBarPage.goToEntity('author');
    authorComponentsPage = new AuthorComponentsPage();
    await browser.wait(ec.visibilityOf(authorComponentsPage.title), 5000);
    expect(await authorComponentsPage.getTitle()).to.eq('Authors');
    await browser.wait(ec.or(ec.visibilityOf(authorComponentsPage.entities), ec.visibilityOf(authorComponentsPage.noResult)), 1000);
  });

  it('should load create Author page', async () => {
    await authorComponentsPage.clickOnCreateButton();
    authorUpdatePage = new AuthorUpdatePage();
    expect(await authorUpdatePage.getPageTitle()).to.eq('Create or edit a Author');
    await authorUpdatePage.cancel();
  });

  it('should create and save Authors', async () => {
    const nbButtonsBeforeCreate = await authorComponentsPage.countDeleteButtons();

    await authorComponentsPage.clickOnCreateButton();

    await promise.all([authorUpdatePage.setFirstNameInput('firstName'), authorUpdatePage.setLastNameInput('lastName')]);

    expect(await authorUpdatePage.getFirstNameInput()).to.eq('firstName', 'Expected FirstName value to be equals to firstName');
    expect(await authorUpdatePage.getLastNameInput()).to.eq('lastName', 'Expected LastName value to be equals to lastName');

    await authorUpdatePage.save();
    expect(await authorUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await authorComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Author', async () => {
    const nbButtonsBeforeDelete = await authorComponentsPage.countDeleteButtons();
    await authorComponentsPage.clickOnLastDeleteButton();

    authorDeleteDialog = new AuthorDeleteDialog();
    expect(await authorDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Author?');
    await authorDeleteDialog.clickOnConfirmButton();

    expect(await authorComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
