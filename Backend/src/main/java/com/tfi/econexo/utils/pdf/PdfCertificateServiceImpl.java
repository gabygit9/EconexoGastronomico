package com.tfi.econexo.utils.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.ReceivedItem;
import com.tfi.econexo.model.donation.ReceptionRecord;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;

@Service
public class PdfCertificateServiceImpl implements  PdfCertificateService {

    @Override
    public byte[] generateCertificate(ReceptionRecord record) {
        Donation donation = record.getDonation();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            //estilos
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            //header
            document.add(new Paragraph("CERTIFICADO DE DONACIÓN ").setFont(bold).setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("(Emitido en el marco legal del Régimen Especial de la Ley N° 25.989 y Ley N° 27.454)").setFont(regular).setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            //info general
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("N° de Certificado: ECN-2026" + record.getAcceptanceTimestamp().getYear() +"-" + record.getId()));
            document.add(new Paragraph("Fecha de Entrega: " + record.getAcceptanceTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")) + " hs.").setFont(regular));

            //datos donante/ong
            addSection(document, "DATOS DEL DONANTE", bold);
            document.add(new Paragraph("Razón Social: " + record.getDonation().getDonor().getLegalName()));
            document.add(new Paragraph("CUIL: " + record.getDonation().getDonor().getTaxId()));

            addSection(document, "DATOS DE LA ONG", bold);
            document.add(new Paragraph("Razón Social: " + record.getDonation().getNgo().getNgoName()));
            document.add(new Paragraph("N° Personería Jurídica: " + record.getDonation().getNgo().getLegalPersonalityNumber()));
            document.add(new Paragraph("CUIL: " + record.getDonation().getNgo().getTaxId()));
            document.add(new Paragraph("Codición ARCA: Entidad Exenta en Impuesto a las Ganancias (Art. 81c)."));

            //detalle alimentos - tabla
            addSection(document, "DETALLE DE LOS ALIMENTOS DONADOS  ", bold);
            Table table = new Table(new float[]{2, 5, 2});
            table.addHeaderCell("Lote").addHeaderCell("Descripción").addHeaderCell("Cantidad");

            for(ReceivedItem item : record.getItems()){
                table.addCell("#ECO-" + item.getDonationItem().getId());
                table.addCell(item.getDonationItem().getProduct().getName());
                table.addCell(item.getReceivedQuantity() + " " + item.getDonationItem().getUnitOfMeasure().getDescription());
            }
            document.add(table);

            //deslinde legal
            addSection(document, "DESLINDE DE RESPONSABILIDAD Y RECEPCIÓN", bold);
            document.add(new Paragraph("El Donante declara bajo juramento que los alimentos detallados cumplen con las exigencias de inocuidad del Código Alimentario Argentino. La Organización Receptora declara haber verificado el estado de los mismos, firmando en conformidad la presente recepción y asumiendo la responsabilidad exclusiva sobre su posterior distribución, liberando al Donante de toda responsabilidad civil y penal posterior a este acto (Art. 8 Ley 25.989).").setFont(regular).setFontSize(9));

            //firma
            addSection(document, "FIRMA ELECTRÓNICA DE RECEPCIÓN (ONG)", bold);
            ImageData imageData = ImageDataFactory.create(new URL(record.getSignatureUrl()).openStream().readAllBytes());
            Image signature = new Image(imageData).setWidth(150);
            document.add(signature);

            document.add(new Paragraph("Firmado por: " + record.getReceivedByEmail()).setFont(bold).setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private void addSection(Document doc, String title, PdfFont font){
        doc.add(new Paragraph("\n" + title).setFont(font).setUnderline());
    }
}
