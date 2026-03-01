package com.sphere.community.dto;

import com.sphere.community.CommunityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommunityRequest {

    @NotBlank(message = "Community name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    private String description;
    private String bannerUrl;
    private String avatarUrl;
    private CommunityType type = CommunityType.PUBLIC;
}