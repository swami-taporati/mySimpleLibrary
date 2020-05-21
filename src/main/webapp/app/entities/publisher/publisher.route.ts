import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IPublisher, Publisher } from 'app/shared/model/publisher.model';
import { PublisherService } from './publisher.service';
import { PublisherComponent } from './publisher.component';
import { PublisherDetailComponent } from './publisher-detail.component';
import { PublisherUpdateComponent } from './publisher-update.component';

@Injectable({ providedIn: 'root' })
export class PublisherResolve implements Resolve<IPublisher> {
  constructor(private service: PublisherService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPublisher> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((publisher: HttpResponse<Publisher>) => {
          if (publisher.body) {
            return of(publisher.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Publisher());
  }
}

export const publisherRoute: Routes = [
  {
    path: '',
    component: PublisherComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'Publishers',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PublisherDetailComponent,
    resolve: {
      publisher: PublisherResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Publishers',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PublisherUpdateComponent,
    resolve: {
      publisher: PublisherResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Publishers',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PublisherUpdateComponent,
    resolve: {
      publisher: PublisherResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Publishers',
    },
    canActivate: [UserRouteAccessService],
  },
];
