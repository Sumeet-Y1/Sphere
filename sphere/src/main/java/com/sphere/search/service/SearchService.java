package com.sphere.search.service;

import com.sphere.community.Community;
import com.sphere.community.dto.CommunityResponse;
import com.sphere.community.repository.CommunityRepository;
import com.sphere.post.Post;
import com.sphere.post.dto.PostResponse;
import com.sphere.post.repository.PostRepository;
import com.sphere.search.dto.SearchResponse;
import com.sphere.user.User;
import com.sphere.user.dto.UserResponse;
import com.sphere.user.repository.FollowRepository;
import com.sphere.user.repository.FollowRequestRepository;
import com.sphere.user.repository.UserRepository;
import com.sphere.user.service.UserPrivacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final UserPrivacyService userPrivacyService;

    public SearchResponse search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return SearchResponse.builder()
                    .users(Collections.emptyList())
                    .communities(Collections.emptyList())
                    .posts(Collections.emptyList())
                    .build();
        }

        String normalizedQuery = query.trim();
        User viewer = getCurrentUser();

        List<UserResponse> users = userRepository.findTop8ByUsernameContainingIgnoreCaseOrderByUsernameAsc(normalizedQuery)
                .stream()
                .map(user -> mapUserResponse(user, viewer))
                .toList();

        List<CommunityResponse> communities = communityRepository.findTop6ByNameContainingIgnoreCaseOrderByMemberCountDesc(normalizedQuery)
                .stream()
                .map(this::mapCommunityResponse)
                .toList();

        List<PostResponse> posts = postRepository
                .findTop12ByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(normalizedQuery, normalizedQuery)
                .stream()
                .filter(post -> userPrivacyService.canViewProfile(viewer, post.getAuthor()))
                .limit(8)
                .map(this::mapPostResponse)
                .toList();

        return SearchResponse.builder()
                .users(users)
                .communities(communities)
                .posts(posts)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse mapUserResponse(User user, User viewer) {
        boolean ownProfile = viewer != null && Objects.equals(viewer.getId(), user.getId());
        boolean following = viewer != null
                && !ownProfile
                && followRepository.existsByFollowerAndFollowing(viewer, user);
        boolean requestedFollow = viewer != null
                && !ownProfile
                && followRequestRepository.existsByRequesterAndTarget(viewer, user);
        boolean canViewProfile = userPrivacyService.canViewProfile(viewer, user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(ownProfile ? user.getEmail() : null)
                .bio(canViewProfile ? user.getBio() : null)
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .authProvider(user.getAuthProvider().name())
                .banned(user.isBanned())
                .followersCount(followRepository.countByFollowing(user))
                .followingCount(followRepository.countByFollower(user))
                .privateAccount(user.isPrivateAccount())
                .following(following)
                .requestedFollow(requestedFollow)
                .canViewProfile(canViewProfile)
                .ownProfile(ownProfile)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private CommunityResponse mapCommunityResponse(Community community) {
        return CommunityResponse.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .bannerUrl(community.getBannerUrl())
                .avatarUrl(community.getAvatarUrl())
                .type(community.getType())
                .ownerUsername(community.getOwner().getUsername())
                .memberCount(community.getMemberCount())
                .createdAt(community.getCreatedAt())
                .build();
    }

    private PostResponse mapPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .linkUrl(post.getLinkUrl())
                .type(post.getType())
                .mediaUrls(post.getMediaUrls())
                .mediaType(post.getMediaType() != null ? post.getMediaType().name() : null)
                .authorUsername(post.getAuthor().getUsername())
                .authorAvatarUrl(post.getAuthor().getAvatarUrl())
                .communityName(post.getCommunity().getName())
                .upvotes(post.getUpvotes())
                .downvotes(post.getDownvotes())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
