package com.project.car.artist.repository;

import com.project.car.artist.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findAllByArtistProfileIdOrderBySortOrderAscCreatedAtAsc(Long artistProfileId);
    Optional<PortfolioItem> findByIdAndArtistProfileId(Long id, Long artistProfileId);
}

