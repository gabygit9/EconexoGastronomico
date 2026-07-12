import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import SignaturePad from 'signature_pad';

@Component({
  selector: 'app-signature-pad',
  imports: [],
  template: `<div class="border-2 border-dashed border-gray-200 rounded-2xl p-2 bg-gray-50">
    <canvas #canvas class="w-full h-48 rounded-xl cursor-crosshair"></canvas>
  </div>`,
  styleUrl: './signature-pad.component.css'
})
export class SignaturePadComponent implements AfterViewInit {

  @ViewChild('canvas') canvas!: ElementRef;
  private signaturePad!: SignaturePad;

  ngAfterViewInit() {
    this.signaturePad = new SignaturePad(this.canvas.nativeElement);
  }

  public toDataURL(): string | null {
    return this.signaturePad.isEmpty() ? null : this.signaturePad.toDataURL();
  }

  public clear(){
    this.signaturePad.clear();
  }
}
