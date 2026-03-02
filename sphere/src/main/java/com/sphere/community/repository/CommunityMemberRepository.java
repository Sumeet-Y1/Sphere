package com.sphere.community.repository;

import com.sphere.community.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;




public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    boolean existsByCommunity_IdAndUser_Id(Long communityId, Long userId);
    Optional<CommunityMember> findByCommunity_IdAndUser_Id(Long communityId, Long userId);
    List<CommunityMember> findByUser_Id(Long userId);
    int countByCommunity_Id(Long communityId);
}