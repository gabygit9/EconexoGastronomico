import {ApplicationConfig, LOCALE_ID, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './routes/app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {caseTransformInterceptor} from './core/interceptors/case-transform.interceptor';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideToastr} from 'ngx-toastr';
import {authInterceptor} from './core/interceptors/auth.interceptor';
import {registerLocaleData} from '@angular/common';
import localeEsAr from '@angular/common/locales/es-AR';

registerLocaleData(localeEsAr, 'es-AR');

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([caseTransformInterceptor, authInterceptor])),
    provideAnimations(),
    provideToastr({
      timeOut: 3000,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
      progressBar: true,
    }),
    { provide: LOCALE_ID, useValue: 'es-AR' }
  ]
};


