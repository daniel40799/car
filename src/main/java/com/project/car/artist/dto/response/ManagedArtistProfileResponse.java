package com.project.car.artist.dto.response;

import com.project.car.common.enums.ApprovalStatus;
import com.project.car.common.enums.ArtistType;
import com.project.car.common.enums.ManagerRole;
import com.project.car.common.enums.ManagerStatus;
import com.project.car.common.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class ManagedArtistProfileResponse {

    private Long id;
    private ArtistType artistType;
    private String displayName;
    private String slug;
    private ApprovalStatus approvalStatus;
    private VerificationStatus verificationStatus;
    private boolean featuredFlag;
    private ManagerRole managerRole;
    private ManagerStatus managerStatus;
    private LocalDateTime createdAt;
}