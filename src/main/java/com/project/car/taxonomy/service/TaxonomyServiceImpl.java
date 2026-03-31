package com.project.car.taxonomy.service;

import com.project.car.artist.dto.response.GenreResponse;
import com.project.car.taxonomy.dto.ArtBranchResponse;
import com.project.car.taxonomy.dto.LocalityResponse;
import com.project.car.taxonomy.entity.Genre;
import com.project.car.taxonomy.repository.ArtBranchRepository;
import com.project.car.taxonomy.repository.GenreRepository;
import com.project.car.taxonomy.repository.LocalityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {

    private final LocalityRepository localityRepository;
    private final ArtBranchRepository artBranchRepository;
    private final GenreRepository genreRepository;

    public TaxonomyServiceImpl(
            LocalityRepository localityRepository,
            ArtBranchRepository artBranchRepository,
            GenreRepository genreRepository
    ) {
        this.localityRepository = localityRepository;
        this.artBranchRepository = artBranchRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public List<LocalityResponse> getLocalities() {
        return localityRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(locality -> new LocalityResponse(locality.getId(), locality.getName(), locality.getProvince()))
                .toList();
    }

    @Override
    public List<ArtBranchResponse> getArtBranches() {
        return artBranchRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(branch -> new ArtBranchResponse(branch.getId(), branch.getName(), branch.getSlug()))
                .toList();
    }

    @Override
    public List<GenreResponse> getGenres(Long artBranchId) {
        List<Genre> genres = artBranchId == null
                ? genreRepository.findAllByActiveTrueOrderByNameAsc()
                : genreRepository.findAllByArtBranchIdAndActiveTrue(artBranchId);

        return genres.stream()
                .map(genre -> new GenreResponse(
                        genre.getId(),
                        genre.getArtBranch().getId(),
                        genre.getArtBranch().getName(),
                        genre.getName(),
                        genre.getSlug()
                ))
                .toList();
    }
}

