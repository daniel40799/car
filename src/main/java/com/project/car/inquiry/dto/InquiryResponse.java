package com.project.car.inquiry.dto;

import com.project.car.common.enums.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InquiryResponse {

    private Long id;
    private Long artistProfileId;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private InquiryType inquiryType;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
}

