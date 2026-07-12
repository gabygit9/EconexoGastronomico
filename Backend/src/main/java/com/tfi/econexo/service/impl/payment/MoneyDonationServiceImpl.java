package com.tfi.econexo.service.impl.payment;

import com.tfi.econexo.dto.payment.MoneyDonationDTO;
import com.tfi.econexo.dto.payment.PaymentRequestDTO;
import com.tfi.econexo.model.donation.MoneyDonation;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.ngo.Ngo;
import com.tfi.econexo.repository.donation.MoneyDonationRepository;
import com.tfi.econexo.repository.ngo.NgoRepository;
import com.tfi.econexo.service.donation.DonorService;
import com.tfi.econexo.service.payment.MoneyDonationService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoneyDonationServiceImpl implements MoneyDonationService {

    private final MoneyDonationRepository moneyDonationRepository;
    private final DonorService donorService;
    private final NgoRepository ngoRepository;

    @Override
    @Transactional
    public Long createMoneyDonation(PaymentRequestDTO dto, Optional<String> donorEmail) {
        MoneyDonation donation = new MoneyDonation();
        donation.setAmount(dto.amount());
        donation.setNgo(ngoRepository.findById(dto.ngoId())
                .orElseThrow(() -> new RuntimeException("Ngo not found")));
        donorEmail.flatMap(donorService::findByUserEmail)
                        .ifPresent(donation::setDonor);
        donation.setStatus(DonationStatus.PENDING_PAYMENT);

        return moneyDonationRepository.save(donation).getId();
    }

    @Override
    public Page<MoneyDonationDTO> getDonations(String ngoEmail, DonationStatus status, Pageable pageable) {
        Ngo ngo = ngoRepository.findByUser_Email(ngoEmail)
                .orElseThrow(() -> new RuntimeException("Ngo not found"));

        return moneyDonationRepository.findAll((root, query, cb) -> {
            Predicate p = cb.equal(root.get("ngo").get("id"), ngo.getId());
            if(status != null){
                p = cb.and(p, cb.equal(root.get("status"), status));
            }
            return p;
        }, pageable).map(d -> new MoneyDonationDTO(
                d.getId(),
                d.getAmount(),
                d.getStatus(),
                ngo.getId(),
                d.getDonor() != null ? d.getDonor().getId() : null,
                d.getCreatedDate()
        ));
    }

}
