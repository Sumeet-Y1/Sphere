package com.sphere.user.controller;

import com.sphere.user.dto.UserResponse;
import com.sphere.user.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/{username}/block")
    public ResponseEntity<Void> blockUser(@PathVariable String username) {
        blockService.blockUser(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable String username) {
        blockService.unblockUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<UserResponse>> getBlockedUsers() {
        return ResponseEntity.ok(blockService.getBlockedUsers());
    }
}