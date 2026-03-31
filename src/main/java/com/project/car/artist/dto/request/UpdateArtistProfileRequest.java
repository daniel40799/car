package com.project.car.artist.dto.request;

import com.project.car.common.enums.ArtistType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateArtistProfileRequest {

    private ArtistType artistType;

    @Size(max = 255)
    private String displayName;

    @Size(max = 5000)
    private String bio;

    private Long localityId;

    private Long primaryArtBranchId;

    private Boolean showEmail;
    private Boolean showPhone;
    private Boolean showSocialLinks;
    private Boolean showLocality;
    private Boolean showPortfolio;
    private Boolean showDirectContact;
}

