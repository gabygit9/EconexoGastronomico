import {HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest, HttpResponse} from '@angular/common/http';
import {map, Observable} from 'rxjs';

export const caseTransformInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  let modifiedReq = req;
  if(req.body){
    const transformedBody = toSnakeCase(req.body);
    modifiedReq = req.clone({body: transformedBody});
  }

  return next(modifiedReq).pipe(
    map((event: HttpEvent<unknown>) => {
      if(event instanceof HttpResponse && event.body){
        const transformedBody = toCamelCase(event.body);
        return event.clone({body: transformedBody});
      }
      return event;
    })
  )
}

function toSnakeCase(obj: any): any{
  if(obj === null || obj === undefined){
    return obj;
  }

  if(obj instanceof  Date || obj instanceof  FormData || obj instanceof Blob || obj instanceof File){
    return obj;
  }

  if(Array.isArray(obj)){
    return obj.map(item => toSnakeCase(item));
  }

  if(typeof obj === 'object' && obj.constructor === Object){
    const snakeCaseObj: any = {};
    for(const key in obj){
      if(Object.prototype.hasOwnProperty.call(obj, key)){
        const snakeKey = key.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
        snakeCaseObj[snakeKey] = toSnakeCase(obj[key]);
      }
    }
    return snakeCaseObj;
  }
  return obj;
}

function toCamelCase(obj: any): any{
  if(obj === null || obj === undefined){
    return obj;
  }

  if (obj instanceof Date || obj instanceof FormData || obj instanceof Blob || obj instanceof File) {
    return obj;
  }

  if (Array.isArray(obj)) {
    return obj.map(item => toCamelCase(item));
  }

  if (typeof obj === 'object' && obj.constructor === Object) {
    const camelCaseObj: any = {};
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        const camelKey = key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
        camelCaseObj[camelKey] = toCamelCase(obj[key]);
      }
    }
    return camelCaseObj;
  }

  return obj;
}
