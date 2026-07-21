import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UploadService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/v1/uploads`;

  /**
   * Uploads a file to Cloudinary specifying the sub-folder
   * @param file
   * @param folder
   */
  uploadFile(file: File, folder:string){
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<{url: string}>(`${this.apiUrl}?folder=${folder}`, formData);
  }
}
