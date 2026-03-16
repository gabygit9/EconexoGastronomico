import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './routes/app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { caseTransformInterceptor } from './shared/interceptors/case-transform.interceptors';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([caseTransformInterceptor])
    ),
  ]
};
