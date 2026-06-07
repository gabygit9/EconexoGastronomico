import {Component, Pipe, PipeTransform} from '@angular/core';


@Pipe({
  name: 'filterByCategory',
  standalone: true
})
export class FilterByCategoryPipe implements PipeTransform {
  transform(products: any[], categoryId: any): any[] {
    if (!categoryId) return [];
    return products.filter(p => p.categoryId === Number(categoryId));
  }
}
