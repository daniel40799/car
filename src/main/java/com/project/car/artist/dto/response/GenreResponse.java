package com.project.car.artist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenreResponse {
    private Long id;
    private Long artBranchId;
    private String artBranchName;
    private String name;
    private String slug;
}

