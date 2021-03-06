import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { MySimpleLibrarySharedModule } from 'app/shared/shared.module';
import { MySimpleLibraryCoreModule } from 'app/core/core.module';
import { MySimpleLibraryAppRoutingModule } from './app-routing.module';
import { MySimpleLibraryHomeModule } from './home/home.module';
import { MySimpleLibraryEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';
// import { CallbackPipe } from './shared/pipes/filter.pipe';

@NgModule({
  imports: [
    BrowserModule,
    MySimpleLibrarySharedModule,
    MySimpleLibraryCoreModule,
    MySimpleLibraryHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    MySimpleLibraryEntityModule,
    MySimpleLibraryAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class MySimpleLibraryAppModule {}
