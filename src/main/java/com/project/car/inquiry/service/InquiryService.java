package com.project.car.inquiry.service;

import com.project.car.inquiry.dto.CreateInquiryRequest;
import com.project.car.inquiry.dto.InquiryResponse;

import java.util.List;

public interface InquiryService {
    InquiryResponse submitInquiry(Long artistProfileId, CreateInquiryRequest request);
    List<InquiryResponse> getInquiriesForProfile(Long userId, Long artistProfileId);
}

