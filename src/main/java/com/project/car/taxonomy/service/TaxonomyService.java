package com.project.car.taxonomy.service;

import com.project.car.artist.dto.response.GenreResponse;
import com.project.car.taxonomy.dto.ArtBranchResponse;
import com.project.car.taxonomy.dto.LocalityResponse;

import java.util.List;

public interface TaxonomyService {
    List<LocalityResponse> getLocalities();
    List<ArtBranchResponse> getArtBranches();
    List<GenreResponse> getGenres(Long artBranchId);
}

