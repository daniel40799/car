package com.project.car.artist.repository;

import com.project.car.artist.entity.ArtistProfileManager;
import com.project.car.common.enums.ManagerStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistProfileManagerRepository extends JpaRepository<ArtistProfileManager, Long> {

    @EntityGraph(attributePaths = {"artistProfile"})
    List<ArtistProfileManager> findAllByUserIdOrderByAssignedAtDesc(Long userId);

    Optional<ArtistProfileManager> findByUserIdAndArtistProfileId(Long userId, Long artistProfileId);

    Optional<ArtistProfileManager> findByUserIdAndArtistProfileIdAndStatus(Long userId, Long artistProfileId, ManagerStatus status);

    boolean existsByUserIdAndArtistProfileId(Long userId, Long artistProfileId);
}