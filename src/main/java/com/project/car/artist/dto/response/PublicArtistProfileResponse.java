package com.project.car.artist.dto.response;

import com.project.car.common.enums.ArtistType;
import com.project.car.common.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PublicArtistProfileResponse {

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
    private String bannerImageUrl;

    private boolean showEmail;
    private boolean showPhone;
    private boolean showSocialLinks;
    private boolean showLocality;
    private boolean showPortfolio;
    private boolean showDirectContact;

    private List<GenreResponse> genres;
    private List<SocialLinkResponse> socialLinks;
    private List<PortfolioItemResponse> portfolioItems;
}


