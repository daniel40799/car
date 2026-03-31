package com.project.car.artist.dto.response;

import com.project.car.common.enums.PortfolioItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PortfolioItemResponse {

    private Long id;
    private Long artistProfileId;
    private PortfolioItemType type;
    private String title;
    private String url;
    private String description;
    private boolean visible;
    private int sortOrder;
    private LocalDateTime createdAt;
}

