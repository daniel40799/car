package com.project.car.inquiry.dto;

import com.project.car.common.enums.InquiryType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInquiryRequest {

    @NotBlank
    @Size(max = 255)
    private String senderName;

    @NotBlank
    @Email
    @Size(max = 255)
    private String senderEmail;

    @Size(max = 100)
    private String senderPhone;

    @NotNull
    private InquiryType inquiryType;

    @Size(max = 255)
    private String subject;

    @NotBlank
    @Size(max = 10000)
    private String message;
}

