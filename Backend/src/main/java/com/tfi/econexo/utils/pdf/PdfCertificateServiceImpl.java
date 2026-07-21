package com.tfi.econexo.utils.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.ReceivedItem;
import com.tfi.econexo.model.donation.ReceptionRecord;
import com.tfi.econexo.model.ngo.Ngo;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;

@Service
public class PdfCertificateServiceImpl implements PdfCertificateService {

    private static final DeviceRgb BLUE_ECO = new DeviceRgb(22, 28, 40);      // #161c28
    private static final DeviceRgb ORANGE_ECO = new DeviceRgb(235, 92, 12);   // #eb5c0c
    private static final DeviceRgb GRAY_BG = new DeviceRgb(248, 250, 252);    // #f8fafc
    private static final DeviceRgb GRAY_BORDER = new DeviceRgb(226, 232, 240);// #e2e8f0
    private static final DeviceRgb GRAY_TEXT = new DeviceRgb(100, 116, 139);  // #64748b


    @Override
    public byte[] generateCertificate(ReceptionRecord record) {
        Donation donation = record.getDonation();
        Ngo ngo = donation.getNgo();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.addNewPage();

            String idCertificate = String.format("ECN-%d-%06d", record.getAcceptanceTimestamp().getYear(), record.getId());
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterEventHandler(idCertificate));

            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(0, 40, 40, 40);

            //estilos
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            //header
            addBrandHeader(document, pdf, bold, regular, idCertificate, record);
            document.add(new Paragraph("\n").setFontSize(4));


            // ---------- DATOS DEL DONANTE / ONG (dos columnas) ----------
            addSectionTitle(document, "PARTES INTERVINIENTES", bold);

            Table partiesTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            partiesTable.setMarginBottom(15);
            partiesTable.addCell(buildPartyCard("ORGANIZACIÓN RECEPTORA", new String[][]{
                    {"Razón Social", ngo.getNgoName()},
                    {"N° Personería Jurídica", ngo.getLegalPersonalityNumber()},
                    {"CUIL", ngo.getTaxId()},
                    {"Condición ARCA", "Entidad Exenta (Art. 81c)"}
            }, bold, regular));
            partiesTable.addCell(buildPartyCard("DONANTE", new String[][]{
                    {"Razón Social", donation.getDonor().getLegalName()},
                    {"CUIL", donation.getDonor().getTaxId()}
            }, bold, regular));
            document.add(partiesTable);

            // ---------- DETALLE DE ALIMENTOS ----------
            addSectionTitle(document, "DETALLE DE LOS ALIMENTOS DONADOS", bold);

            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 5, 2})).useAllAvailableWidth();
            table.setMarginBottom(15);
            table.addHeaderCell(headerCell("Lote", bold));
            table.addHeaderCell(headerCell("Descripción", bold));
            table.addHeaderCell(headerCell("Cantidad", bold));

            boolean alternate = false;
            for (ReceivedItem item : record.getItems()) {
                DeviceRgb rowColor = alternate ? GRAY_BG : (DeviceRgb) DeviceRgb.WHITE;
                String baseName = item.getDonationItem().getProduct().getName();
                String finalDescription = (item.getDonationItem().getDescription() != null
                        && !item.getDonationItem().getDescription().isEmpty())
                        ? item.getDonationItem().getDescription()
                        : baseName;

                table.addCell(bodyCell("#ECO-" + item.getDonationItem().getId(), regular, rowColor));
                table.addCell(bodyCell(finalDescription, regular, rowColor));
                table.addCell(bodyCell(item.getReceivedQuantity() + " " + item.getDonationItem().getUnitOfMeasure().getDescription(), regular, rowColor));
                alternate = !alternate;
            }
            document.add(table);

            // ---------- DESLINDE LEGAL ----------
            addSectionTitle(document, "DESLINDE DE RESPONSABILIDAD Y RECEPCIÓN", bold);

            Div legalBox = new Div()
                    .setBackgroundColor(GRAY_BG)
                    .setBorder(new SolidBorder(GRAY_BORDER, 1))
                    .setPadding(12)
                    .setMarginBottom(20);
            legalBox.add(new Paragraph(
                    "El Donante declara bajo juramento que los alimentos detallados cumplen con las exigencias de " +
                            "inocuidad del Código Alimentario Argentino. La Organización Receptora declara haber verificado " +
                            "el estado de los mismos, firmando en conformidad la presente recepción y asumiendo la " +
                            "responsabilidad exclusiva sobre su posterior distribución, liberando al Donante de toda " +
                            "responsabilidad civil y penal posterior a este acto (Art. 8 Ley 25.989).")
                    .setFont(italic).setFontSize(9).setFontColor(GRAY_TEXT).setMultipliedLeading(1.3f));
            document.add(legalBox);

            // ---------- FIRMA ----------
            document.add(buildSignatureBlock(record, ngo, bold, regular));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private void addBrandHeader(Document document, PdfDocument pdf, PdfFont bold, PdfFont regular,
                                String certificadoId, ReceptionRecord record) {
        PdfPage page = pdf.getFirstPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas canvas = new PdfCanvas(page);

        float barHeight = 85;
        float filletHeight = 4;

        // Barra principal azul EcoNexo
        canvas.saveState()
                .setFillColor(BLUE_ECO)
                .rectangle(0, pageSize.getTop() - barHeight, pageSize.getWidth(), barHeight)
                .fill()
                .restoreState();

        // Filete naranja debajo de la barra
        canvas.saveState()
                .setFillColor(ORANGE_ECO)
                .rectangle(0, pageSize.getTop() - barHeight - filletHeight, pageSize.getWidth(), filletHeight)
                .fill()
                .restoreState();

        Div headerContent = new Div()
                .setFixedPosition(40, pageSize.getTop() - barHeight, pageSize.getWidth() - 80)
                .setHeight(barHeight);

        DeviceRgb regimenGray = new DeviceRgb(148, 163, 184);

        Div innerContent = new Div();

        Table headerLayout = new Table(UnitValue.createPercentArray(new float[]{0.9f, 3.2f, 0.9f})).useAllAvailableWidth();

        Cell brandCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        brandCell.add(new Paragraph()
                .add(new Text("ECO").setFont(bold).setFontColor(DeviceRgb.WHITE).setFontSize(16))
                .add(new Text("NEXO").setFont(bold).setFontColor(ORANGE_ECO).setFontSize(16)));
        brandCell.add(new Paragraph("Logística Social Solidaria")
                .setFont(regular).setFontColor(new DeviceRgb(203, 213, 225)).setFontSize(7.5f).setMarginTop(-6));
        headerLayout.addCell(brandCell);

        Cell titleCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        titleCell.add(new Paragraph("CERTIFICADO DE DONACIÓN")
                .setFont(bold).setFontColor(DeviceRgb.WHITE).setFontSize(18).setCharacterSpacing(0.2f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(1f));
        headerLayout.addCell(titleCell);

        Cell certCell = new Cell().setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT);
        certCell.add(new Paragraph(certificadoId)
                .setFont(bold).setFontColor(ORANGE_ECO).setFontSize(10));
        certCell.add(new Paragraph(record.getAcceptanceTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm 'hs.'")))
                .setFont(regular).setFontColor(new DeviceRgb(203, 213, 225)).setFontSize(8).setMarginTop(-2));
        headerLayout.addCell(certCell);

        innerContent.add(headerLayout);
        innerContent.add(new Paragraph("Régimen de la Ley N° 25.989 y Ley N° 27.454")
                .setFont(regular).setFontColor(regimenGray).setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(-7));

        Table outer = new Table(1).useAllAvailableWidth();
        Cell outerCell = new Cell().setBorder(Border.NO_BORDER)
                .setHeight(barHeight)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(0);
        outerCell.add(innerContent);
        outer.addCell(outerCell);

        headerContent.add(outer);

        document.add(headerContent);

        document.add(new Div().setHeight(barHeight + filletHeight + 20));
    }

    private void addSectionTitle(Document document, String title, PdfFont bold) {
        Div titleDiv = new Div().setMarginTop(4).setMarginBottom(8);
        Table t = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        Cell c = new Cell().setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(ORANGE_ECO, 1.5f))
                .setPaddingBottom(4);
        c.add(new Paragraph(title).setFont(bold).setFontSize(11).setFontColor(BLUE_ECO).setCharacterSpacing(0.5f));
        t.addCell(c);
        titleDiv.add(t);
        document.add(titleDiv);
    }

    private Cell buildPartyCard(String label, String[][] fields, PdfFont bold, PdfFont regular) {
        Cell card = new Cell().setBorder(Border.NO_BORDER).setPadding(4);
        Div box = new Div()
                .setBackgroundColor(GRAY_BG)
                .setBorder(new SolidBorder(GRAY_BORDER, 1))
                .setPadding(10);

        box.add(new Paragraph(label).setFont(bold).setFontSize(9).setFontColor(ORANGE_ECO)
                .setCharacterSpacing(0.5f).setMarginBottom(4));

        for (String[] field : fields) {
            Paragraph p = new Paragraph().setMarginBottom(2).setFontSize(9.5f);
            p.add(new Text(field[0] + ": ").setFont(bold).setFontColor(BLUE_ECO));
            p.add(new Text(field[1]).setFont(regular).setFontColor(GRAY_TEXT));
            box.add(p);
        }
        card.add(box);
        return card;
    }

    private Cell headerCell(String text, PdfFont bold) {
        return new Cell()
                .add(new Paragraph(text).setFont(bold).setFontColor(DeviceRgb.WHITE).setFontSize(9.5f))
                .setBackgroundColor(ORANGE_ECO)
                .setPadding(7)
                .setBorder(Border.NO_BORDER);
    }

    private Cell bodyCell(String text, PdfFont regular, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(text).setFont(regular).setFontSize(9.5f).setFontColor(BLUE_ECO))
                .setBackgroundColor(bgColor)
                .setPadding(7)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(GRAY_BORDER, 0.5f));
    }

    private Div buildSignatureBlock(ReceptionRecord record, Ngo ngo, PdfFont bold, PdfFont regular) throws Exception {
        Div signatureBox = new Div()
                .setTextAlignment(TextAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(UnitValue.createPercentValue(60));

        signatureBox.add(new Paragraph("RESPONSABLE DE RECEPCIÓN")
                .setFont(bold).setFontSize(9).setFontColor(GRAY_TEXT).setCharacterSpacing(0.5f)
                .setTextAlignment(TextAlignment.CENTER));

        ImageData imageData = ImageDataFactory.create(new URL(record.getSignatureUrl()).openStream().readAllBytes());
        Image signature = new Image(imageData).setWidth(110)
                .setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginTop(8);
        signatureBox.add(signature);

        signatureBox.add(new Div().setHeight(1).setWidth(150)
                .setBackgroundColor(GRAY_BORDER).setMarginTop(4)
                .setHorizontalAlignment(HorizontalAlignment.CENTER));

        signatureBox.add(new Paragraph(ngo.getResponsibleName())
                .setFont(bold).setFontSize(11).setFontColor(BLUE_ECO).setMarginTop(6)
                .setTextAlignment(TextAlignment.CENTER));
        signatureBox.add(new Paragraph(ngo.getUser().getEmail() + "  ·  " + ngo.getPhoneNumber())
                .setFont(regular).setFontSize(9).setFontColor(GRAY_TEXT).setMarginTop(2)
                .setTextAlignment(TextAlignment.CENTER));

        Div wrapper = new Div().setTextAlignment(TextAlignment.CENTER).setMarginTop(15);
        wrapper.add(signatureBox);
        return wrapper;
    }

    /**
     * Dibuja pie de página (número de página + leyenda de validez) en cada página del documento.
     */
    private static class FooterEventHandler implements IEventHandler {
        private final String certificadoId;

        FooterEventHandler(String certificadoId) {
            this.certificadoId = certificadoId;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            PdfDocument pdf = docEvent.getDocument();
            Rectangle pageSize = page.getPageSize();
            int pageNumber = pdf.getPageNumber(page);

            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .setStrokeColor(GRAY_BORDER)
                    .setLineWidth(0.5f)
                    .moveTo(40, 35)
                    .lineTo(pageSize.getWidth() - 40, 35)
                    .stroke()
                    .restoreState();

            try (Document doc = new Document(pdf)) {
                doc.setMargins(0, 0, 0, 0);
                PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

                Paragraph left = new Paragraph("Documento generado electrónicamente por EcoNexo · " + certificadoId)
                        .setFont(regular).setFontSize(7.5f).setFontColor(GRAY_TEXT)
                        .setFixedPosition(40, 18, 350);
                doc.add(left);

                Paragraph right = new Paragraph("Página " + pageNumber)
                        .setFont(regular).setFontSize(7.5f).setFontColor(GRAY_TEXT)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFixedPosition(pageSize.getWidth() - 140, 18, 100);
                doc.add(right);
            } catch (Exception e) {
                // No interrumpir la generación del PDF por un fallo en el footer
            }
        }
    }
}
