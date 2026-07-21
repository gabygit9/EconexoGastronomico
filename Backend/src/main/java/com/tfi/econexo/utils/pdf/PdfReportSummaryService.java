package com.tfi.econexo.utils.pdf;

import java.time.LocalDate;

public interface PdfReportSummaryService {
    byte[] generateSummaryReport(Long donorId, LocalDate start, LocalDate end);
}
