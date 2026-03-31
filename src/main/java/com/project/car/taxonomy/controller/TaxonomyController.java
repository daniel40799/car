package com.project.car.taxonomy.controller;

import com.project.car.artist.dto.response.GenreResponse;
import com.project.car.taxonomy.dto.ArtBranchResponse;
import com.project.car.taxonomy.dto.LocalityResponse;
import com.project.car.taxonomy.service.TaxonomyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/taxonomy")
public class TaxonomyController {

    private final TaxonomyService taxonomyService;

    public TaxonomyController(TaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;
    }

    @GetMapping("/localities")
    public List<LocalityResponse> getLocalities() {
        return taxonomyService.getLocalities();
    }

    @GetMapping("/art-branches")
    public List<ArtBranchResponse> getArtBranches() {
        return taxonomyService.getArtBranches();
    }

    @GetMapping("/genres")
    public List<GenreResponse> getGenres(@RequestParam(required = false) Long artBranchId) {
        return taxonomyService.getGenres(artBranchId);
    }
}

