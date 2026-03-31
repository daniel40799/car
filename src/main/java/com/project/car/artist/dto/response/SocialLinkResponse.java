package com.project.car.artist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SocialLinkResponse {

    private Long id;
    private Long artistProfileId;
    private String platform;
    private String url;
    private boolean visible;
    private LocalDateTime createdAt;
}

