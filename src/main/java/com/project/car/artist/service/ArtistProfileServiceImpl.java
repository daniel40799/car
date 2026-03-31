package com.project.car.artist.service;

import com.project.car.artist.dto.request.CreateArtistProfileRequest;
import com.project.car.artist.dto.request.CreatePortfolioItemRequest;
import com.project.car.artist.dto.request.CreateSocialLinkRequest;
import com.project.car.artist.dto.request.UpdateArtistProfileRequest;
import com.project.car.artist.dto.request.UpdatePortfolioItemRequest;
import com.project.car.artist.dto.request.UpdateSocialLinkRequest;
import com.project.car.artist.dto.response.ArtistProfileResponse;
import com.project.car.artist.dto.response.GenreResponse;
import com.project.car.artist.dto.response.ManagedArtistProfileResponse;
import com.project.car.artist.dto.response.PortfolioItemResponse;
import com.project.car.artist.dto.response.SocialLinkResponse;
import com.project.car.artist.entity.ArtistProfile;
import com.project.car.artist.entity.ArtistProfileManager;
import com.project.car.artist.entity.PortfolioItem;
import com.project.car.artist.entity.SocialLink;
import com.project.car.artist.repository.ArtistProfileManagerRepository;
import com.project.car.artist.repository.ArtistProfileRepository;
import com.project.car.artist.repository.PortfolioItemRepository;
import com.project.car.artist.repository.SocialLinkRepository;
import com.project.car.common.enums.ApprovalStatus;
import com.project.car.common.enums.ManagerRole;
import com.project.car.common.enums.ManagerStatus;
import com.project.car.common.enums.VerificationStatus;
import com.project.car.common.exception.NotFoundException;
import com.project.car.taxonomy.entity.ArtBranch;
import com.project.car.taxonomy.entity.Genre;
import com.project.car.taxonomy.entity.Locality;
import com.project.car.taxonomy.repository.ArtBranchRepository;
import com.project.car.taxonomy.repository.GenreRepository;
import com.project.car.taxonomy.repository.LocalityRepository;
import com.project.car.user.entity.User;
import com.project.car.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class ArtistProfileServiceImpl implements ArtistProfileService {

    private final ArtistProfileRepository artistProfileRepository;
    private final ArtistProfileManagerRepository artistProfileManagerRepository;
    private final UserRepository userRepository;
    private final LocalityRepository localityRepository;
    private final ArtBranchRepository artBranchRepository;
    private final SocialLinkRepository socialLinkRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final GenreRepository genreRepository;

    public ArtistProfileServiceImpl(
            ArtistProfileRepository artistProfileRepository,
            ArtistProfileManagerRepository artistProfileManagerRepository,
            UserRepository userRepository,
            LocalityRepository localityRepository,
            ArtBranchRepository artBranchRepository,
            SocialLinkRepository socialLinkRepository,
            PortfolioItemRepository portfolioItemRepository,
            GenreRepository genreRepository
    ) {
        this.artistProfileRepository = artistProfileRepository;
        this.artistProfileManagerRepository = artistProfileManagerRepository;
        this.userRepository = userRepository;
        this.localityRepository = localityRepository;
        this.artBranchRepository = artBranchRepository;
        this.socialLinkRepository = socialLinkRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public ArtistProfileResponse createArtistProfile(Long userId, CreateArtistProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Locality locality = null;
        if (request.getLocalityId() != null) {
            locality = localityRepository.findById(request.getLocalityId())
                    .orElseThrow(() -> new NotFoundException("Locality not found: " + request.getLocalityId()));
        }

        ArtBranch artBranch = null;
        if (request.getPrimaryArtBranchId() != null) {
            artBranch = artBranchRepository.findById(request.getPrimaryArtBranchId())
                    .orElseThrow(() -> new NotFoundException("Art branch not found: " + request.getPrimaryArtBranchId()));
        }

        String slug = generateUniqueSlug(request.getDisplayName());

        ArtistProfile profile = new ArtistProfile();
        profile.setArtistType(request.getArtistType());
        profile.setDisplayName(request.getDisplayName().trim());
        profile.setSlug(slug);
        profile.setBio(trimToNull(request.getBio()));
        profile.setLocality(locality);
        profile.setPrimaryArtBranch(artBranch);

        profile.setApprovalStatus(ApprovalStatus.PENDING);
        profile.setVerificationStatus(VerificationStatus.UNVERIFIED);

        profile.setFeaturedFlag(false);
        profile.setProfileImageUrl(null);
        profile.setBannerImageUrl(null);

        profile.setShowEmail(Boolean.TRUE.equals(request.getShowEmail()));
        profile.setShowPhone(Boolean.TRUE.equals(request.getShowPhone()));
        profile.setShowSocialLinks(request.getShowSocialLinks() == null || request.getShowSocialLinks());
        profile.setShowLocality(request.getShowLocality() == null || request.getShowLocality());
        profile.setShowPortfolio(request.getShowPortfolio() == null || request.getShowPortfolio());
        profile.setShowDirectContact(Boolean.TRUE.equals(request.getShowDirectContact()));

        ArtistProfile savedProfile = artistProfileRepository.save(profile);

        ArtistProfileManager manager = new ArtistProfileManager();
        manager.setUser(user);
        manager.setArtistProfile(savedProfile);
        manager.setManagerRole(ManagerRole.OWNER);
        manager.setStatus(ManagerStatus.ACTIVE);

        artistProfileManagerRepository.save(manager);

        return toArtistProfileResponse(savedProfile);
    }

    @Override
    public List<ManagedArtistProfileResponse> getMyManagedProfiles(Long userId) {
        return artistProfileManagerRepository.findAllByUserIdOrderByAssignedAtDesc(userId)
                .stream()
                .map(manager -> new ManagedArtistProfileResponse(
                        manager.getArtistProfile().getId(),
                        manager.getArtistProfile().getArtistType(),
                        manager.getArtistProfile().getDisplayName(),
                        manager.getArtistProfile().getSlug(),
                        manager.getArtistProfile().getApprovalStatus(),
                        manager.getArtistProfile().getVerificationStatus(),
                        manager.getArtistProfile().isFeaturedFlag(),
                        manager.getManagerRole(),
                        manager.getStatus(),
                        manager.getArtistProfile().getCreatedAt()
                ))
                .toList();
    }

    @Override
    public ArtistProfileResponse getManagedProfileById(Long userId, Long profileId) {
        ArtistProfileManager manager = getActiveManagerOrThrow(userId, profileId);
        return toArtistProfileResponse(manager.getArtistProfile());
    }

    @Override
    @Transactional
    public ArtistProfileResponse updateManagedProfile(Long userId, Long profileId, UpdateArtistProfileRequest request) {
        ArtistProfileManager manager = getActiveManagerOrThrow(userId, profileId);
        ArtistProfile profile = manager.getArtistProfile();

        if (request.getArtistType() != null) {
            profile.setArtistType(request.getArtistType());
        }
        if (request.getDisplayName() != null) {
            profile.setDisplayName(request.getDisplayName().trim());
        }
        if (request.getBio() != null) {
            profile.setBio(trimToNull(request.getBio()));
        }
        if (request.getLocalityId() != null) {
            Locality locality = localityRepository.findById(request.getLocalityId())
                    .orElseThrow(() -> new NotFoundException("Locality not found: " + request.getLocalityId()));
            profile.setLocality(locality);
        }
        if (request.getPrimaryArtBranchId() != null) {
            ArtBranch artBranch = artBranchRepository.findById(request.getPrimaryArtBranchId())
                    .orElseThrow(() -> new NotFoundException("Art branch not found: " + request.getPrimaryArtBranchId()));
            profile.setPrimaryArtBranch(artBranch);
        }

        if (request.getShowEmail() != null) {
            profile.setShowEmail(request.getShowEmail());
        }
        if (request.getShowPhone() != null) {
            profile.setShowPhone(request.getShowPhone());
        }
        if (request.getShowSocialLinks() != null) {
            profile.setShowSocialLinks(request.getShowSocialLinks());
        }
        if (request.getShowLocality() != null) {
            profile.setShowLocality(request.getShowLocality());
        }
        if (request.getShowPortfolio() != null) {
            profile.setShowPortfolio(request.getShowPortfolio());
        }
        if (request.getShowDirectContact() != null) {
            profile.setShowDirectContact(request.getShowDirectContact());
        }

        ArtistProfile saved = artistProfileRepository.save(profile);
        return toArtistProfileResponse(saved);
    }

    @Override
    @Transactional
    public SocialLinkResponse addSocialLink(Long userId, Long profileId, CreateSocialLinkRequest request) {
        ArtistProfileManager manager = getActiveManagerOrThrow(userId, profileId);

        SocialLink socialLink = new SocialLink();
        socialLink.setArtistProfile(manager.getArtistProfile());
        socialLink.setPlatform(request.getPlatform().trim());
        socialLink.setUrl(request.getUrl().trim());
        socialLink.setVisible(request.getVisible() == null || request.getVisible());

        SocialLink saved = socialLinkRepository.save(socialLink);
        return toSocialLinkResponse(saved);
    }

    @Override
    @Transactional
    public PortfolioItemResponse addPortfolioItem(Long userId, Long profileId, CreatePortfolioItemRequest request) {
        ArtistProfileManager manager = getActiveManagerOrThrow(userId, profileId);

        PortfolioItem item = new PortfolioItem();
        item.setArtistProfile(manager.getArtistProfile());
        item.setType(request.getType());
        item.setTitle(request.getTitle().trim());
        item.setUrl(request.getUrl().trim());
        item.setDescription(trimToNull(request.getDescription()));
        item.setVisible(request.getVisible() == null || request.getVisible());
        item.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        PortfolioItem saved = portfolioItemRepository.save(item);
        return toPortfolioItemResponse(saved);
    }

    @Override
    public List<SocialLinkResponse> getSocialLinks(Long userId, Long profileId) {
        getActiveManagerOrThrow(userId, profileId);
        return socialLinkRepository.findAllByArtistProfileIdOrderByCreatedAtAsc(profileId)
                .stream()
                .map(this::toSocialLinkResponse)
                .toList();
    }

    @Override
    @Transactional
    public SocialLinkResponse updateSocialLink(Long userId, Long profileId, Long linkId, UpdateSocialLinkRequest request) {
        getActiveManagerOrThrow(userId, profileId);
        SocialLink link = socialLinkRepository.findByIdAndArtistProfileId(linkId, profileId)
                .orElseThrow(() -> new NotFoundException("Social link not found: " + linkId));

        if (request.getPlatform() != null) {
            link.setPlatform(request.getPlatform().trim());
        }
        if (request.getUrl() != null) {
            link.setUrl(request.getUrl().trim());
        }
        if (request.getVisible() != null) {
            link.setVisible(request.getVisible());
        }

        return toSocialLinkResponse(socialLinkRepository.save(link));
    }

    @Override
    @Transactional
    public void deleteSocialLink(Long userId, Long profileId, Long linkId) {
        getActiveManagerOrThrow(userId, profileId);
        SocialLink link = socialLinkRepository.findByIdAndArtistProfileId(linkId, profileId)
                .orElseThrow(() -> new NotFoundException("Social link not found: " + linkId));
        socialLinkRepository.delete(link);
    }

    @Override
    public List<PortfolioItemResponse> getPortfolioItems(Long userId, Long profileId) {
        getActiveManagerOrThrow(userId, profileId);
        return portfolioItemRepository.findAllByArtistProfileIdOrderBySortOrderAscCreatedAtAsc(profileId)
                .stream()
                .map(this::toPortfolioItemResponse)
                .toList();
    }

    @Override
    @Transactional
    public PortfolioItemResponse updatePortfolioItem(Long userId, Long profileId, Long itemId, UpdatePortfolioItemRequest request) {
        getActiveManagerOrThrow(userId, profileId);
        PortfolioItem item = portfolioItemRepository.findByIdAndArtistProfileId(itemId, profileId)
                .orElseThrow(() -> new NotFoundException("Portfolio item not found: " + itemId));

        if (request.getType() != null) {
            item.setType(request.getType());
        }
        if (request.getTitle() != null) {
            item.setTitle(request.getTitle().trim());
        }
        if (request.getUrl() != null) {
            item.setUrl(request.getUrl().trim());
        }
        if (request.getDescription() != null) {
            item.setDescription(trimToNull(request.getDescription()));
        }
        if (request.getVisible() != null) {
            item.setVisible(request.getVisible());
        }
        if (request.getSortOrder() != null) {
            item.setSortOrder(request.getSortOrder());
        }

        return toPortfolioItemResponse(portfolioItemRepository.save(item));
    }

    @Override
    @Transactional
    public void deletePortfolioItem(Long userId, Long profileId, Long itemId) {
        getActiveManagerOrThrow(userId, profileId);
        PortfolioItem item = portfolioItemRepository.findByIdAndArtistProfileId(itemId, profileId)
                .orElseThrow(() -> new NotFoundException("Portfolio item not found: " + itemId));
        portfolioItemRepository.delete(item);
    }

    private ArtistProfileResponse toArtistProfileResponse(ArtistProfile profile) {
        return new ArtistProfileResponse(
                profile.getId(),
                profile.getArtistType(),
                profile.getDisplayName(),
                profile.getSlug(),
                profile.getBio(),
                profile.getLocality() != null ? profile.getLocality().getId() : null,
                profile.getLocality() != null ? profile.getLocality().getName() : null,
                profile.getPrimaryArtBranch() != null ? profile.getPrimaryArtBranch().getId() : null,
                profile.getPrimaryArtBranch() != null ? profile.getPrimaryArtBranch().getName() : null,
                profile.getApprovalStatus(),
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
                profile.getCreatedAt()
        );
    }

    private String generateUniqueSlug(String input) {
        String baseSlug = slugify(input);
        String candidate = baseSlug;
        int counter = 2;

        while (artistProfileRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + counter;
            counter++;
        }

        return candidate;
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = normalized
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return slug.isBlank() ? "artist" : slug;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ArtistProfileManager getActiveManagerOrThrow(Long userId, Long profileId) {
        return artistProfileManagerRepository
                .findByUserIdAndArtistProfileIdAndStatus(userId, profileId, ManagerStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Managed profile not found: " + profileId));
    }

    @Override
    public List<GenreResponse> getGenres(Long userId, Long profileId) {
        ArtistProfileManager manager = getActiveManagerOrThrow(userId, profileId);
        return manager.getArtistProfile().getGenres()
                .stream()
                .map(this::toGenreResponse)
                .toList();
    }

    @Override
    @Transactional
    public GenreResponse addGenre(Long userId, Long profileId, Long genreId) {
        ArtistProfileManager manager = getActiveManagerOrThrow(userId, profileId);
        ArtistProfile profile = manager.getArtistProfile();

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found: " + genreId));

        profile.getGenres().add(genre);
        artistProfileRepository.save(profile);
        return toGenreResponse(genre);
    }

    @Override
    @Transactional
    public void removeGenre(Long userId, Long profileId, Long genreId) {
        ArtistProfileManager manager = getActiveManagerOrThrow(userId, profileId);
        ArtistProfile profile = manager.getArtistProfile();

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found: " + genreId));

        profile.getGenres().remove(genre);
        artistProfileRepository.save(profile);
    }

    private GenreResponse toGenreResponse(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getArtBranch().getId(),
                genre.getArtBranch().getName(),
                genre.getName(),
                genre.getSlug()
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