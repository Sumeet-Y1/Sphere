package com.sphere.search.dto;

import com.sphere.community.dto.CommunityResponse;
import com.sphere.post.dto.PostResponse;
import com.sphere.user.dto.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResponse {
    private List<UserResponse> users;
    private List<CommunityResponse> communities;
    private List<PostResponse> posts;
}
