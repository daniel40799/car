package com.project.car.inquiry.controller;

import com.project.car.inquiry.dto.CreateInquiryRequest;
import com.project.car.inquiry.dto.InquiryResponse;
import com.project.car.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    /**
     * Public endpoint — anyone can submit an inquiry to an approved artist profile.
     */
    @PostMapping("/api/v1/public/artist-profiles/{artistProfileId}/inquiries")
    @ResponseStatus(HttpStatus.CREATED)
    public InquiryResponse submitInquiry(
            @PathVariable Long artistProfileId,
            @Valid @RequestBody CreateInquiryRequest request
    ) {
        return inquiryService.submitInquiry(artistProfileId, request);
    }

    /**
     * Manager endpoint — only active managers can view their profile's inquiries.
     */
    @GetMapping("/api/v1/artist-profiles/{id}/inquiries")
    public List<InquiryResponse> getInquiries(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return inquiryService.getInquiriesForProfile(userId, id);
    }
}

