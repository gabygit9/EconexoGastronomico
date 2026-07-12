package com.tfi.econexo.utils.pdf;

import com.tfi.econexo.model.donation.ReceptionRecord;

public interface PdfCertificateService {
    byte[] generateCertificate(ReceptionRecord record);
}
