package com.project.car.artist.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSocialLinkRequest {

    @Size(max = 100)
    private String platform;

    @Size(max = 2000)
    private String url;

    private Boolean visible;
}

