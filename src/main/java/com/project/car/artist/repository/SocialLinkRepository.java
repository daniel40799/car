package com.project.car.artist.repository;

import com.project.car.artist.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {
    List<SocialLink> findAllByArtistProfileIdOrderByCreatedAtAsc(Long artistProfileId);
    Optional<SocialLink> findByIdAndArtistProfileId(Long id, Long artistProfileId);
}

