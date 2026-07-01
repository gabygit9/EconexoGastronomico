import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import SignaturePad from 'signature_pad';

@Component({
  selector: 'app-signature-pad',
  imports: [],
  template: `<canvas #canvas style="border: 1px solid #000; width: 100%; height: 200px;"></canvas>`,
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
