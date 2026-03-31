package com.project.car.artist.entity;
import com.project.car.common.enums.ManagerRole;
import com.project.car.common.enums.ManagerStatus;
import com.project.car.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "artist_profile_managers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_artist_profile_managers_user_profile", columnNames = {"user_id", "artist_profile_id"})
        }
)
public class ArtistProfileManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_profile_id", nullable = false)
    private ArtistProfile artistProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_role", nullable = false, length = 50)
    private ManagerRole managerRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ManagerStatus status;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}