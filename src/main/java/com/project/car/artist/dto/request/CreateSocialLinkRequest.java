package com.project.car.artist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSocialLinkRequest {

    @NotBlank
    @Size(max = 100)
    private String platform;

    @NotBlank
    @Size(max = 2000)
    private String url;

    private Boolean visible;
}

