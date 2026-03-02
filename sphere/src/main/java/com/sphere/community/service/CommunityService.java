package com.sphere.community.service;

import com.sphere.community.Community;
import com.sphere.community.CommunityMember;
import com.sphere.community.MemberRole;
import com.sphere.community.dto.CommunityResponse;
import com.sphere.community.dto.CreateCommunityRequest;
import com.sphere.community.repository.CommunityMemberRepository;
import com.sphere.community.repository.CommunityRepository;
import com.sphere.user.User;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMemberRepository communityMemberRepository;

    public CommunityResponse createCommunity(CreateCommunityRequest request) {
        if (communityRepository.existsByName(request.getName())) {
            throw new RuntimeException("Community name already taken");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Community community = Community.builder()
                .name(request.getName())
                .description(request.getDescription())
                .bannerUrl(request.getBannerUrl())
                .avatarUrl(request.getAvatarUrl())
                .type(request.getType())
                .owner(owner)
                .memberCount(1)
                .build();

        communityRepository.save(community);

        CommunityMember ownerMember = CommunityMember.builder()
                .community(community)
                .user(owner)
                .role(MemberRole.OWNER)
                .build();
        communityMemberRepository.save(ownerMember);

        return mapToResponse(community);
    }

    public CommunityResponse getCommunity(String name) {
        Community community = communityRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        return mapToResponse(community);
    }

    public List<CommunityResponse> getAllCommunities() {
        return communityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CommunityResponse> getMyCommunities() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return communityRepository.findByOwner_Id(owner.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CommunityResponse joinCommunity(Long communityId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        if (communityMemberRepository.existsByCommunity_IdAndUser_Id(communityId, user.getId())) {
            throw new RuntimeException("Already a member");
        }

        CommunityMember member = CommunityMember.builder()
                .community(community)
                .user(user)
                .role(MemberRole.MEMBER)
                .build();

        communityMemberRepository.save(member);
        community.setMemberCount(community.getMemberCount() + 1);
        communityRepository.save(community);

        return mapToResponse(community);
    }

    public void leaveCommunity(Long communityId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommunityMember member = communityMemberRepository
                .findByCommunity_IdAndUser_Id(communityId, user.getId())
                .orElseThrow(() -> new RuntimeException("Not a member"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        communityMemberRepository.delete(member);
        community.setMemberCount(community.getMemberCount() - 1);
        communityRepository.save(community);
    }

    private CommunityResponse mapToResponse(Community community) {
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
}