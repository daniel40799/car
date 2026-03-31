package com.project.car.artist.service;

import com.project.car.artist.dto.response.GenreResponse;
import com.project.car.artist.dto.response.PortfolioItemResponse;
import com.project.car.artist.dto.response.PublicArtistProfileResponse;
import com.project.car.artist.dto.response.PublicArtistProfileSummary;
import com.project.car.artist.dto.response.SocialLinkResponse;
import com.project.car.artist.entity.ArtistProfile;
import com.project.car.artist.entity.PortfolioItem;
import com.project.car.artist.entity.SocialLink;
import com.project.car.artist.repository.ArtistProfileRepository;
import com.project.car.artist.repository.PortfolioItemRepository;
import com.project.car.artist.repository.SocialLinkRepository;
import com.project.car.common.enums.ApprovalStatus;
import com.project.car.common.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicDirectoryServiceImpl implements PublicDirectoryService {

    private final ArtistProfileRepository artistProfileRepository;
    private final SocialLinkRepository socialLinkRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public PublicDirectoryServiceImpl(
            ArtistProfileRepository artistProfileRepository,
            SocialLinkRepository socialLinkRepository,
            PortfolioItemRepository portfolioItemRepository
    ) {
        this.artistProfileRepository = artistProfileRepository;
        this.socialLinkRepository = socialLinkRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @Override
    public Page<PublicArtistProfileSummary> searchProfiles(
            String keyword, Long localityId, Long artBranchId, Long genreId, int page, int size
    ) {
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "featuredFlag")
                        .and(Sort.by(Sort.Direction.ASC, "displayName")));

        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        return artistProfileRepository
                .findPublicProfiles(ApprovalStatus.APPROVED, kw, localityId, artBranchId, genreId, pageable)
                .map(this::toSummary);
    }

    @Override
    public PublicArtistProfileResponse getProfileBySlug(String slug) {
        ArtistProfile profile = artistProfileRepository
                .findBySlugAndApprovalStatus(slug, ApprovalStatus.APPROVED)
                .orElseThrow(() -> new NotFoundException("Artist profile not found: " + slug));

        List<GenreResponse> genres = profile.getGenres()
                .stream()
                .map(genre -> new GenreResponse(
                        genre.getId(),
                        genre.getArtBranch().getId(),
                        genre.getArtBranch().getName(),
                        genre.getName(),
                        genre.getSlug()
                ))
                .toList();

        List<SocialLinkResponse> socialLinks = profile.isShowSocialLinks()
                ? socialLinkRepository.findAllByArtistProfileIdOrderByCreatedAtAsc(profile.getId())
                        .stream()
                        .filter(SocialLink::isVisible)
                        .map(this::toSocialLinkResponse)
                        .toList()
                : List.of();

        List<PortfolioItemResponse> portfolioItems = profile.isShowPortfolio()
                ? portfolioItemRepository.findAllByArtistProfileIdOrderBySortOrderAscCreatedAtAsc(profile.getId())
                        .stream()
                        .filter(PortfolioItem::isVisible)
                        .map(this::toPortfolioItemResponse)
                        .toList()
                : List.of();

        return new PublicArtistProfileResponse(
                profile.getId(),
                profile.getArtistType(),
                profile.getDisplayName(),
                profile.getSlug(),
                profile.getBio(),
                profile.getLocality() != null ? profile.getLocality().getId() : null,
                profile.getLocality() != null ? profile.getLocality().getName() : null,
                profile.getPrimaryArtBranch() != null ? profile.getPrimaryArtBranch().getId() : null,
                profile.getPrimaryArtBranch() != null ? profile.getPrimaryArtBranch().getName() : null,
                profile.getVerificationStatus(),
                profile.isFeaturedFlag(),
                profile.getProfileImageUrl(),
                profile.getBannerImageUrl(),
                profile.isShowEmail(),
                profile.isShowPhone(),
                profile.isShowSocialLinks(),
                profile.isShowLocality(),
                profile.isShowPortfolio(),
                profile.isShowDirectContact(),
                genres,
                socialLinks,
                portfolioItems
        );
    }

    private PublicArtistProfileSummary toSummary(ArtistProfile p) {
        return new PublicArtistProfileSummary(
                p.getId(),
                p.getArtistType(),
                p.getDisplayName(),
                p.getSlug(),
                p.getBio(),
                p.getLocality() != null ? p.getLocality().getId() : null,
                p.getLocality() != null ? p.getLocality().getName() : null,
                p.getPrimaryArtBranch() != null ? p.getPrimaryArtBranch().getId() : null,
                p.getPrimaryArtBranch() != null ? p.getPrimaryArtBranch().getName() : null,
                p.getVerificationStatus(),
                p.isFeaturedFlag(),
                p.getProfileImageUrl()
        );
    }

    private SocialLinkResponse toSocialLinkResponse(SocialLink link) {
        return new SocialLinkResponse(
                link.getId(),
                link.getArtistProfile().getId(),
                link.getPlatform(),
                link.getUrl(),
                link.isVisible(),
                link.getCreatedAt()
        );
    }

    private PortfolioItemResponse toPortfolioItemResponse(PortfolioItem item) {
        return new PortfolioItemResponse(
                item.getId(),
                item.getArtistProfile().getId(),
                item.getType(),
                item.getTitle(),
                item.getUrl(),
                item.getDescription(),
                item.isVisible(),
                item.getSortOrder(),
                item.getCreatedAt()
        );
    }
}


