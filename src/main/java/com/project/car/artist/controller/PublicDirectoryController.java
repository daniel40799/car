package com.project.car.artist.controller;

import com.project.car.artist.dto.response.PublicArtistProfileResponse;
import com.project.car.artist.dto.response.PublicArtistProfileSummary;
import com.project.car.artist.service.PublicDirectoryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/artists")
public class PublicDirectoryController {

    private final PublicDirectoryService publicDirectoryService;

    public PublicDirectoryController(PublicDirectoryService publicDirectoryService) {
        this.publicDirectoryService = publicDirectoryService;
    }

    @GetMapping
    public Page<PublicArtistProfileSummary> searchArtists(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long localityId,
            @RequestParam(required = false) Long artBranchId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return publicDirectoryService.searchProfiles(keyword, localityId, artBranchId, genreId, page, size);
    }

    @GetMapping("/{slug}")
    public PublicArtistProfileResponse getArtistBySlug(@PathVariable String slug) {
        return publicDirectoryService.getProfileBySlug(slug);
    }
}


