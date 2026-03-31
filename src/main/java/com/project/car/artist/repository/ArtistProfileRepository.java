package com.project.car.artist.repository;

import com.project.car.artist.entity.ArtistProfile;
import com.project.car.common.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, Long> {
    Optional<ArtistProfile> findBySlug(String slug);
    boolean existsBySlug(String slug);

    Optional<ArtistProfile> findBySlugAndApprovalStatus(String slug, ApprovalStatus approvalStatus);

    @Query(
            value = """
                    SELECT DISTINCT p FROM ArtistProfile p
                    LEFT JOIN p.genres g
                    WHERE p.approvalStatus = :status
                      AND (:keyword IS NULL
                           OR LOWER(p.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(p.bio) LIKE LOWER(CONCAT('%', :keyword, '%')))
                      AND (:localityId IS NULL OR p.locality.id = :localityId)
                      AND (:artBranchId IS NULL OR p.primaryArtBranch.id = :artBranchId)
                      AND (:genreId IS NULL OR g.id = :genreId)
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT p.id) FROM ArtistProfile p
                    LEFT JOIN p.genres g
                    WHERE p.approvalStatus = :status
                      AND (:keyword IS NULL
                           OR LOWER(p.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                           OR LOWER(p.bio) LIKE LOWER(CONCAT('%', :keyword, '%')))
                      AND (:localityId IS NULL OR p.locality.id = :localityId)
                      AND (:artBranchId IS NULL OR p.primaryArtBranch.id = :artBranchId)
                      AND (:genreId IS NULL OR g.id = :genreId)
                    """
    )
    Page<ArtistProfile> findPublicProfiles(
            @Param("status") ApprovalStatus status,
            @Param("keyword") String keyword,
            @Param("localityId") Long localityId,
            @Param("artBranchId") Long artBranchId,
            @Param("genreId") Long genreId,
            Pageable pageable
    );
}