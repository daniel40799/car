package com.project.car.artist.service;

import com.project.car.artist.dto.response.PublicArtistProfileResponse;
import com.project.car.artist.dto.response.PublicArtistProfileSummary;
import org.springframework.data.domain.Page;

public interface PublicDirectoryService {
    Page<PublicArtistProfileSummary> searchProfiles(String keyword, Long localityId, Long artBranchId, Long genreId, int page, int size);
    PublicArtistProfileResponse getProfileBySlug(String slug);
}


