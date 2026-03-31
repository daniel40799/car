package com.project.car.artist.controller;

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
import com.project.car.artist.service.ArtistProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artist-profiles")
public class ArtistProfileController {

    private final ArtistProfileService artistProfileService;

    public ArtistProfileController(ArtistProfileService artistProfileService) {
        this.artistProfileService = artistProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistProfileResponse createArtistProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateArtistProfileRequest request
    ) {
        return artistProfileService.createArtistProfile(userId, request);
    }

    @GetMapping("/me/managed")
    public List<ManagedArtistProfileResponse> getMyManagedProfiles(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return artistProfileService.getMyManagedProfiles(userId);
    }

    @GetMapping("/{id}")
    public ArtistProfileResponse getManagedProfileById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return artistProfileService.getManagedProfileById(userId, id);
    }

    @PatchMapping("/{id}")
    public ArtistProfileResponse updateManagedProfile(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateArtistProfileRequest request
    ) {
        return artistProfileService.updateManagedProfile(userId, id, request);
    }

    // Social Links

    @GetMapping("/{id}/social-links")
    public List<SocialLinkResponse> getSocialLinks(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return artistProfileService.getSocialLinks(userId, id);
    }

    @PostMapping("/{id}/social-links")
    @ResponseStatus(HttpStatus.CREATED)
    public SocialLinkResponse addSocialLink(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CreateSocialLinkRequest request
    ) {
        return artistProfileService.addSocialLink(userId, id, request);
    }

    @PatchMapping("/{id}/social-links/{linkId}")
    public SocialLinkResponse updateSocialLink(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @PathVariable Long linkId,
            @Valid @RequestBody UpdateSocialLinkRequest request
    ) {
        return artistProfileService.updateSocialLink(userId, id, linkId, request);
    }

    @DeleteMapping("/{id}/social-links/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSocialLink(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @PathVariable Long linkId
    ) {
        artistProfileService.deleteSocialLink(userId, id, linkId);
    }

    // Portfolio Items

    @GetMapping("/{id}/portfolio-items")
    public List<PortfolioItemResponse> getPortfolioItems(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return artistProfileService.getPortfolioItems(userId, id);
    }

    @PostMapping("/{id}/portfolio-items")
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioItemResponse addPortfolioItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CreatePortfolioItemRequest request
    ) {
        return artistProfileService.addPortfolioItem(userId, id, request);
    }

    @PatchMapping("/{id}/portfolio-items/{itemId}")
    public PortfolioItemResponse updatePortfolioItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdatePortfolioItemRequest request
    ) {
        return artistProfileService.updatePortfolioItem(userId, id, itemId, request);
    }

    @DeleteMapping("/{id}/portfolio-items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolioItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @PathVariable Long itemId
    ) {
        artistProfileService.deletePortfolioItem(userId, id, itemId);
    }

    // Genres

    @GetMapping("/{id}/genres")
    public List<GenreResponse> getGenres(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return artistProfileService.getGenres(userId, id);
    }

    @PostMapping("/{id}/genres/{genreId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GenreResponse addGenre(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @PathVariable Long genreId
    ) {
        return artistProfileService.addGenre(userId, id, genreId);
    }

    @DeleteMapping("/{id}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGenre(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @PathVariable Long genreId
    ) {
        artistProfileService.removeGenre(userId, id, genreId);
    }
}


