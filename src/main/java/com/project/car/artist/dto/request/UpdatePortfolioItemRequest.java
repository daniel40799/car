package com.project.car.artist.dto.request;

import com.project.car.common.enums.PortfolioItemType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePortfolioItemRequest {

    private PortfolioItemType type;

    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String url;

    @Size(max = 5000)
    private String description;

    private Boolean visible;

    private Integer sortOrder;
}

