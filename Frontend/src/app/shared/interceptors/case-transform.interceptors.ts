import {
  HttpRequest,
  HttpHandlerFn,
  HttpEvent,
  HttpResponse
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export function caseTransformInterceptor(
  request: HttpRequest<unknown>, 
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  // Transform request body to snake_case
  if (request.body) {
    const transformedBody = toSnakeCase(request.body);
    request = request.clone({ body: transformedBody });
  }

  return next(request).pipe(
    map((event) => {
      if (event instanceof HttpResponse) {
        // Transform response body to camelCase
        if (event.body) {
          const transformedBody = toCamelCase(event.body);
          return event.clone({ body: transformedBody });
        }
      }
      return event;
    })
  );
}

function toSnakeCase(obj: any): any {
  if (obj === null || obj === undefined) {
    return obj;
  }

  if (obj instanceof Date) {
    return obj;
  }

  if (Array.isArray(obj)) {
    return obj.map(item => toSnakeCase(item));
  }

  if (typeof obj === 'object' && obj.constructor === Object) {
    const snakeCaseObj: any = {};
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        const snakeKey = key.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
        snakeCaseObj[snakeKey] = toSnakeCase(obj[key]);
      }
    }
    return snakeCaseObj;
  }

  return obj;
}

function toCamelCase(obj: any): any {
  if (obj === null || obj === undefined) {
    return obj;
  }

  if (obj instanceof Date) {
    return obj;
  }

  if (Array.isArray(obj)) {
    return obj.map(item => toCamelCase(item));
  }

  if (typeof obj === 'object' && obj.constructor === Object) {
    const camelCaseObj: any = {};
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        const camelKey = key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
        camelCaseObj[camelKey] = toCamelCase(obj[key]);
      }
    }
    return camelCaseObj;
  }

  return obj;
} 