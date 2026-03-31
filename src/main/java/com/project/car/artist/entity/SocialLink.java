package com.project.car.artist.entity;

import com.project.car.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "social_links")
public class SocialLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_profile_id", nullable = false)
    private ArtistProfile artistProfile;

    @Column(nullable = false, length = 100)
    private String platform;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "is_visible", nullable = false)
    private boolean visible = true;
}