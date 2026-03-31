package com.project.car.taxonomy.entity;

import com.project.car.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "genres",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_genres_branch_name", columnNames = {"art_branch_id", "name"}),
                @UniqueConstraint(name = "uq_genres_branch_slug", columnNames = {"art_branch_id", "slug"})
        }
)
public class Genre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_branch_id", nullable = false)
    private ArtBranch artBranch;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String slug;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}