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

import java.util.List;

public interface ArtistProfileService {

    ArtistProfileResponse createArtistProfile(Long userId, CreateArtistProfileRequest request);

    List<ManagedArtistProfileResponse> getMyManagedProfiles(Long userId);

    ArtistProfileResponse getManagedProfileById(Long userId, Long profileId);

    ArtistProfileResponse updateManagedProfile(Long userId, Long profileId, UpdateArtistProfileRequest request);

    // Social links
    SocialLinkResponse addSocialLink(Long userId, Long profileId, CreateSocialLinkRequest request);

    List<SocialLinkResponse> getSocialLinks(Long userId, Long profileId);

    SocialLinkResponse updateSocialLink(Long userId, Long profileId, Long linkId, UpdateSocialLinkRequest request);

    void deleteSocialLink(Long userId, Long profileId, Long linkId);

    // Portfolio items
    PortfolioItemResponse addPortfolioItem(Long userId, Long profileId, CreatePortfolioItemRequest request);

    List<PortfolioItemResponse> getPortfolioItems(Long userId, Long profileId);

    PortfolioItemResponse updatePortfolioItem(Long userId, Long profileId, Long itemId, UpdatePortfolioItemRequest request);

    void deletePortfolioItem(Long userId, Long profileId, Long itemId);

    // Genres
    List<GenreResponse> getGenres(Long userId, Long profileId);

    GenreResponse addGenre(Long userId, Long profileId, Long genreId);

    void removeGenre(Long userId, Long profileId, Long genreId);
}