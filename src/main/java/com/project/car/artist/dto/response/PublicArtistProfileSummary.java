package com.project.car.artist.dto.response;

import com.project.car.common.enums.ArtistType;
import com.project.car.common.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublicArtistProfileSummary {

    private Long id;
    private ArtistType artistType;
    private String displayName;
    private String slug;
    private String bio;

    private Long localityId;
    private String localityName;

    private Long primaryArtBranchId;
    private String primaryArtBranchName;

    private VerificationStatus verificationStatus;
    private boolean featuredFlag;

    private String profileImageUrl;
}

