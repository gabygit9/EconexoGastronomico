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
import com.tfi.econexo.model.donation.ReceptionRecord;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.repository.donation.ReceptionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfReportSummaryServiceImpl implements  PdfReportSummaryService {
    private final ReceptionRecordRepository receptionRecordRepository;

    private static final DeviceRgb BLUE_ECO = new DeviceRgb(22, 28, 40);
    private static final DeviceRgb ORANGE_ECO = new DeviceRgb(235, 92, 12);
    private static final DeviceRgb GRAY_BG = new DeviceRgb(248, 250, 252);
    private static final DeviceRgb GRAY_BORDER = new DeviceRgb(226, 232, 240);
    private static final DeviceRgb GRAY_TEXT = new DeviceRgb(100, 116, 139);
    private static final DeviceRgb LIGHT_BLUE_TEXT = new DeviceRgb(203, 213, 225);
    private static final DeviceRgb REGIMEN_GRAY = new DeviceRgb(148, 163, 184);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    @Override
    public byte[] generateSummaryReport(Long donorId, LocalDate start, LocalDate end) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(23, 59, 59);

            List<ReceptionRecord> records = receptionRecordRepository
                    .findByDonation_Donor_IdAndAcceptanceTimestampBetween(donorId, startDateTime, endDateTime);

            if (records.isEmpty()) {
                throw new RuntimeException("There are not donations for the selected period");
            }

            records.sort(Comparator.comparing(ReceptionRecord::getAcceptanceTimestamp).reversed());

            Donor donor = records.get(0).getDonation().getDonor();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.addNewPage(PageSize.A4.rotate());

            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterEventHandler());

            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(0, 30, 40, 30);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            addBrandHeader(document, pdf, bold, regular, start, end);

            addSectionTitle(document, "DATOS DEL DONANTE", bold);
            document.add(buildDonorCard(donor, bold, regular));

            addSectionTitle(document, "DETALLE DE DONACIONES DEL PERÍODO", bold);
            document.add(buildRecordsTable(records, bold, regular));

            addSectionTitle(document, "DESLINDE DE RESPONSABILIDAD", bold);
            document.add(buildDisclaimerBox(italic));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error in report", e);
        }
    }

    private void addBrandHeader(Document document, PdfDocument pdf, PdfFont bold, PdfFont regular,
                                LocalDate start, LocalDate end) {
        PdfPage page = pdf.getFirstPage();
        Rectangle pageRect = page.getPageSize();
        PdfCanvas canvas = new PdfCanvas(page);

        float barHeight = 80;
        float filletHeight = 4;

        canvas.saveState().setFillColor(BLUE_ECO)
                .rectangle(0, pageRect.getTop() - barHeight, pageRect.getWidth(), barHeight)
                .fill().restoreState();

        canvas.saveState().setFillColor(ORANGE_ECO)
                .rectangle(0, pageRect.getTop() - barHeight - filletHeight, pageRect.getWidth(), filletHeight)
                .fill().restoreState();

        Div innerContent = new Div();

        Table headerLayout = new Table(UnitValue.createPercentArray(new float[]{1f, 3f, 1f})).useAllAvailableWidth();

        Cell brandCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        brandCell.add(new Paragraph()
                .add(new Text("ECO").setFont(bold).setFontColor(DeviceRgb.WHITE).setFontSize(16))
                .add(new Text("NEXO").setFont(bold).setFontColor(ORANGE_ECO).setFontSize(16)));
        brandCell.add(new Paragraph("Logística Social Solidaria")
                .setFont(regular).setFontColor(LIGHT_BLUE_TEXT).setFontSize(7.5f).setMarginTop(-6));
        headerLayout.addCell(brandCell);

        Cell titleCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        titleCell.add(new Paragraph("REPORTE UNIFICADO DE DONACIONES")
                .setFont(bold).setFontColor(DeviceRgb.WHITE).setFontSize(17).setCharacterSpacing(0.1f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(1f));
        headerLayout.addCell(titleCell);
        Cell spacerCell = new Cell().setBorder(Border.NO_BORDER);
        headerLayout.addCell(spacerCell);

        innerContent.add(headerLayout);
        innerContent.add(new Paragraph("Período: " + start.format(DATE_FMT) + " al " + end.format(DATE_FMT))
                .setFont(regular).setFontColor(REGIMEN_GRAY).setFontSize(8.5f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(2));

        Table outer = new Table(1).useAllAvailableWidth();
        Cell outerCell = new Cell().setBorder(Border.NO_BORDER)
                .setHeight(barHeight).setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0);
        outerCell.add(innerContent);
        outer.addCell(outerCell);

        Div headerContent = new Div()
                .setFixedPosition(30, pageRect.getTop() - barHeight, pageRect.getWidth() - 60)
                .setHeight(barHeight);
        headerContent.add(outer);

        document.add(headerContent);
        document.add(new Div().setHeight(barHeight + filletHeight + 18));
    }

    private void addSectionTitle(Document document, String title, PdfFont bold) {
        Table t = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        Cell c = new Cell().setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(ORANGE_ECO, 1.5f))
                .setPaddingBottom(4);
        c.add(new Paragraph(title).setFont(bold).setFontSize(10.5f).setFontColor(BLUE_ECO).setCharacterSpacing(0.4f));
        t.addCell(c);
        Div wrapper = new Div().setMarginTop(2).setMarginBottom(6);
        wrapper.add(t);
        document.add(wrapper);
    }

    private Div buildDonorCard(Donor donor, PdfFont bold, PdfFont regular) {
        Div box = new Div()
                .setBackgroundColor(GRAY_BG)
                .setBorder(new SolidBorder(GRAY_BORDER, 1))
                .setPadding(10)
                .setMarginBottom(12);

        Table row = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();

        Cell left = new Cell().setBorder(Border.NO_BORDER);
        Paragraph p1 = new Paragraph().setFontSize(9.5f);
        p1.add(new Text("Razón Social: ").setFont(bold).setFontColor(BLUE_ECO));
        p1.add(new Text(donor.getLegalName()).setFont(regular).setFontColor(GRAY_TEXT));
        left.add(p1);
        row.addCell(left);

        Cell right = new Cell().setBorder(Border.NO_BORDER);
        Paragraph p2 = new Paragraph().setFontSize(9.5f);
        p2.add(new Text("CUIL: ").setFont(bold).setFontColor(BLUE_ECO));
        p2.add(new Text(donor.getTaxId()).setFont(regular).setFontColor(GRAY_TEXT));
        right.add(p2);
        row.addCell(right);

        box.add(row);
        return box;
    }

    private Table buildRecordsTable(List<ReceptionRecord> records, PdfFont bold, PdfFont regular) throws Exception {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1.3f, 2.3f, 2.5f, 1, 2.4f, 1.3f}))
                .useAllAvailableWidth();

        table.addHeaderCell(headerCell("Lote", bold));
        table.addHeaderCell(headerCell("Fecha y Hora", bold));
        table.addHeaderCell(headerCell("ONG", bold));
        table.addHeaderCell(headerCell("Detalle", bold));
        table.addHeaderCell(headerCell("Cant.", bold));
        table.addHeaderCell(headerCell("Responsable", bold));
        table.addHeaderCell(headerCell("Firma", bold));

        boolean alternate = false;
        for (ReceptionRecord record : records) {
            DeviceRgb rowColor = alternate ? GRAY_BG : (DeviceRgb) DeviceRgb.WHITE;
            var ngo = record.getDonation().getNgo();

            // Lote: uno por record
            String lote = "#ECO-" + record.getId();
            table.addCell(bodyCell(lote, regular, rowColor));

            table.addCell(bodyCell(record.getAcceptanceTimestamp().format(DATETIME_FMT), regular, rowColor));

            Paragraph ngoP = new Paragraph().setFontSize(7.5f).setMultipliedLeading(1.25f);
            ngoP.add(new Text(ngo.getNgoName() + "\n").setFont(bold).setFontColor(BLUE_ECO));
            ngoP.add(new Text("Pers. Jurídica: " + ngo.getLegalPersonalityNumber() + "\n").setFont(regular).setFontColor(GRAY_TEXT));
            ngoP.add(new Text("CUIL: " + ngo.getTaxId() + "\n").setFont(regular).setFontColor(GRAY_TEXT));
            ngoP.add(new Text("ARCA: Exenta (Art. 81c)").setFont(regular).setFontColor(GRAY_TEXT));
            table.addCell(new Cell().add(ngoP).setBackgroundColor(rowColor).setPadding(6).setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(GRAY_BORDER, 0.5f)));

            StringBuilder items = new StringBuilder();
            StringBuilder quants = new StringBuilder();
            record.getItems().forEach(i -> {
                String baseName = i.getDonationItem().getProduct().getName();
                String desc = (i.getDonationItem().getDescription() != null && !i.getDonationItem().getDescription().isEmpty())
                        ? i.getDonationItem().getDescription() : baseName;
                items.append(desc).append("\n");
                quants.append(i.getReceivedQuantity()).append(" ")
                        .append(abbreviateUnit(i.getDonationItem().getUnitOfMeasure().getDescription())).append("\n");
            });
            table.addCell(bodyCell(items.toString().trim(), regular, rowColor));
            table.addCell(bodyCell(quants.toString().trim(), regular, rowColor));

            Paragraph contact = new Paragraph().setFontSize(7.5f).setMultipliedLeading(1.25f);
            contact.add(new Text(ngo.getResponsibleName() + "\n").setFont(bold).setFontColor(BLUE_ECO));
            contact.add(new Text(ngo.getUser().getEmail() + "\n").setFont(regular).setFontColor(GRAY_TEXT));
            contact.add(new Text(ngo.getPhoneNumber()).setFont(regular).setFontColor(GRAY_TEXT));
            table.addCell(new Cell().add(contact).setBackgroundColor(rowColor).setPadding(6).setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(GRAY_BORDER, 0.5f)));

            Cell signatureCell = new Cell().setBackgroundColor(rowColor).setPadding(4).setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(GRAY_BORDER, 0.5f))
                    .setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
            if (record.getSignatureUrl() != null && !record.getSignatureUrl().isEmpty()) {
                ImageData imgData = ImageDataFactory.create(new URL(record.getSignatureUrl()).openStream().readAllBytes());
                signatureCell.add(new Image(imgData).setWidth(38).setHorizontalAlignment(HorizontalAlignment.CENTER));
            } else {
                signatureCell.add(new Paragraph("Sin firma").setFont(regular).setFontSize(7).setFontColor(GRAY_TEXT));
            }
            table.addCell(signatureCell);

            alternate = !alternate;
        }

        return table;
    }

    private Cell headerCell(String text, PdfFont bold) {
        return new Cell()
                .add(new Paragraph(text).setFont(bold).setFontColor(DeviceRgb.WHITE).setFontSize(8.5f))
                .setBackgroundColor(ORANGE_ECO)
                .setPadding(6)
                .setBorder(Border.NO_BORDER);
    }

    private Cell bodyCell(String text, PdfFont regular, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(text).setFont(regular).setFontSize(7.5f).setFontColor(BLUE_ECO).setMultipliedLeading(1.2f))
                .setBackgroundColor(bgColor)
                .setPadding(6)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(GRAY_BORDER, 0.5f));
    }

    private Div buildDisclaimerBox(PdfFont italic) {
        Div legalBox = new Div()
                .setBackgroundColor(GRAY_BG)
                .setBorder(new SolidBorder(GRAY_BORDER, 1))
                .setPadding(10)
                .setMarginTop(10);
        legalBox.add(new Paragraph(
                "El presente reporte es un resumen informativo generado automáticamente por la plataforma EcoNexo, " +
                        "que consolida las donaciones efectuadas por el Donante durante el período indicado. Cada entrega " +
                        "detallada cuenta con su respectivo Certificado de Donación individual, firmado en conformidad por el " +
                        "responsable de la Organización Receptora al momento de la recepción, en los términos de la Ley N° " +
                        "25.989 y Ley N° 27.454. EcoNexo actúa exclusivamente como intermediario tecnológico y logístico, sin " +
                        "asumir responsabilidad sobre la calidad, inocuidad o destino posterior de los alimentos donados, " +
                        "responsabilidad que corresponde a las partes intervinientes en cada operación según lo declarado en " +
                        "los certificados individuales correspondientes.")
                .setFont(italic).setFontSize(8f).setFontColor(GRAY_TEXT).setMultipliedLeading(1.3f));
        return legalBox;
    }

    private static class FooterEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            PdfDocument pdf = docEvent.getDocument();
            Rectangle pageRect = page.getPageSize();
            int pageNumber = pdf.getPageNumber(page);

            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState().setStrokeColor(GRAY_BORDER).setLineWidth(0.5f)
                    .moveTo(30, 22).lineTo(pageRect.getWidth() - 30, 22).stroke().restoreState();

            try (Document doc = new Document(pdf)) {
                doc.setMargins(0, 0, 0, 0);
                PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

                doc.add(new Paragraph("Documento generado electrónicamente por EcoNexo")
                        .setFont(regular).setFontSize(7f).setFontColor(GRAY_TEXT)
                        .setFixedPosition(30, 10, 300));

                doc.add(new Paragraph("Página " + pageNumber)
                        .setFont(regular).setFontSize(7f).setFontColor(GRAY_TEXT)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFixedPosition(pageRect.getWidth() - 130, 10, 100));
            } catch (Exception e) {
                // No interrumpir la generación por un fallo en el footer
            }
        }
    }

    private static final Map<String, String> UNIT_ABBREVIATIONS = Map.ofEntries(
            Map.entry("kilogramos", "kg."),
            Map.entry("kilogramo", "kg."),
            Map.entry("porciones", "porc."),
            Map.entry("porcion", "porc."),
            Map.entry("unidades", "U."),
            Map.entry("unidad", "U."),
            Map.entry("litros", "Lts."),
            Map.entry("litro", "Lts.")
    );

    private String abbreviateUnit(String unitDescription) {
        if (unitDescription == null) return "";
        String key = unitDescription.trim().toLowerCase();
        return UNIT_ABBREVIATIONS.getOrDefault(key, unitDescription);
    }
}
