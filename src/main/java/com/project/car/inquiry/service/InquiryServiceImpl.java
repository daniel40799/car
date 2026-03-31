package com.project.car.inquiry.service;

import com.project.car.artist.entity.ArtistProfile;
import com.project.car.artist.repository.ArtistProfileManagerRepository;
import com.project.car.artist.repository.ArtistProfileRepository;
import com.project.car.common.enums.ApprovalStatus;
import com.project.car.common.enums.ManagerStatus;
import com.project.car.common.exception.BadRequestException;
import com.project.car.common.exception.NotFoundException;
import com.project.car.inquiry.dto.CreateInquiryRequest;
import com.project.car.inquiry.dto.InquiryResponse;
import com.project.car.inquiry.entity.Inquiry;
import com.project.car.inquiry.repository.InquiryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final ArtistProfileManagerRepository artistProfileManagerRepository;

    public InquiryServiceImpl(
            InquiryRepository inquiryRepository,
            ArtistProfileRepository artistProfileRepository,
            ArtistProfileManagerRepository artistProfileManagerRepository
    ) {
        this.inquiryRepository = inquiryRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.artistProfileManagerRepository = artistProfileManagerRepository;
    }

    @Override
    @Transactional
    public InquiryResponse submitInquiry(Long artistProfileId, CreateInquiryRequest request) {
        ArtistProfile profile = artistProfileRepository.findById(artistProfileId)
                .orElseThrow(() -> new NotFoundException("Artist profile not found: " + artistProfileId));

        if (profile.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Artist profile is not publicly available.");
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setArtistProfile(profile);
        inquiry.setSenderName(request.getSenderName().trim());
        inquiry.setSenderEmail(request.getSenderEmail().trim().toLowerCase());
        inquiry.setSenderPhone(request.getSenderPhone() != null ? request.getSenderPhone().trim() : null);
        inquiry.setInquiryType(request.getInquiryType());
        inquiry.setSubject(request.getSubject() != null ? request.getSubject().trim() : null);
        inquiry.setMessage(request.getMessage().trim());

        return toResponse(inquiryRepository.save(inquiry));
    }

    @Override
    public List<InquiryResponse> getInquiriesForProfile(Long userId, Long artistProfileId) {
        artistProfileManagerRepository
                .findByUserIdAndArtistProfileIdAndStatus(userId, artistProfileId, ManagerStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Managed profile not found: " + artistProfileId));

        return inquiryRepository.findAllByArtistProfileIdOrderByCreatedAtDesc(artistProfileId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private InquiryResponse toResponse(Inquiry inquiry) {
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getArtistProfile().getId(),
                inquiry.getSenderName(),
                inquiry.getSenderEmail(),
                inquiry.getSenderPhone(),
                inquiry.getInquiryType(),
                inquiry.getSubject(),
                inquiry.getMessage(),
                inquiry.getCreatedAt()
        );
    }
}

