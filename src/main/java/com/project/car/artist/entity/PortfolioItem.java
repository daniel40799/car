package com.project.car.artist.entity;
import com.project.car.common.entity.BaseEntity;
import com.project.car.common.enums.PortfolioItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "portfolio_items")
public class PortfolioItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_profile_id", nullable = false)
    private ArtistProfile artistProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PortfolioItemType type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_visible", nullable = false)
    private boolean visible = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}