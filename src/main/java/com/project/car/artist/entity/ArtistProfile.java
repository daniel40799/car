package com.project.car.artist.entity;

import com.project.car.common.entity.BaseEntity;
import com.project.car.common.enums.ApprovalStatus;
import com.project.car.common.enums.ArtistType;
import com.project.car.common.enums.VerificationStatus;
import com.project.car.taxonomy.entity.ArtBranch;
import com.project.car.taxonomy.entity.Genre;
import com.project.car.taxonomy.entity.Locality;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "artist_profiles")
public class ArtistProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "artist_type", nullable = false, length = 50)
    private ArtistType artistType;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id")
    private Locality locality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_art_branch_id")
    private ArtBranch primaryArtBranch;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 50)
    private ApprovalStatus approvalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 50)
    private VerificationStatus verificationStatus;

    @Column(name = "featured_flag", nullable = false)
    private boolean featuredFlag = false;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(name = "banner_image_url", columnDefinition = "TEXT")
    private String bannerImageUrl;

    @Column(name = "show_email", nullable = false)
    private boolean showEmail = false;

    @Column(name = "show_phone", nullable = false)
    private boolean showPhone = false;

    @Column(name = "show_social_links", nullable = false)
    private boolean showSocialLinks = true;

    @Column(name = "show_locality", nullable = false)
    private boolean showLocality = true;

    @Column(name = "show_portfolio", nullable = false)
    private boolean showPortfolio = true;

    @Column(name = "show_direct_contact", nullable = false)
    private boolean showDirectContact = false;

    @ManyToMany
    @JoinTable(
            name = "artist_profile_genres",
            joinColumns = @JoinColumn(name = "artist_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
}