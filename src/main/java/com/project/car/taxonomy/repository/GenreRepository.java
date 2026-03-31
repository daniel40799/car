package com.project.car.taxonomy.repository;

import com.project.car.taxonomy.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findAllByActiveTrueOrderByNameAsc();
    List<Genre> findAllByArtBranchIdAndActiveTrue(Long artBranchId);
}


