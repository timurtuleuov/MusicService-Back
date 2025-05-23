package com.timur.spotify.controller.music;

import com.timur.spotify.dto.TrackFeedDTO;
import com.timur.spotify.service.music.TrackFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/track-feeds")
@RequiredArgsConstructor
public class TrackFeedController {

    private final TrackFeedService trackFeedService;

    // Получить все фиды (без учёта лайков)
    @GetMapping
    public ResponseEntity<List<TrackFeedDTO>> getAllFeeds() {
        List<TrackFeedDTO> feeds = trackFeedService.getAllFeeds();
        return ResponseEntity.ok(feeds);
    }

    // Получить все фиды с лайками для пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TrackFeedDTO>> getFeedsForUser(@PathVariable Long userId) {
        List<TrackFeedDTO> feeds = trackFeedService.getAllFeedsForUser(userId);
        return ResponseEntity.ok(feeds);
    }

    // Получить один фид по ID
    @GetMapping("/{id}")
    public ResponseEntity<TrackFeedDTO> getFeedById(@PathVariable Long id) {
        return trackFeedService.getFeedById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать новый фид
    @PostMapping
    public ResponseEntity<TrackFeedDTO> createFeed(@RequestBody TrackFeedDTO dto) {
        TrackFeedDTO created = trackFeedService.createFeed(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Обновить фид
    @PutMapping("/{id}")
    public ResponseEntity<TrackFeedDTO> updateFeed(@PathVariable Long id, @RequestBody TrackFeedDTO dto) {
        TrackFeedDTO updated = trackFeedService.updateFeed(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Удалить фид
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Long id) {
        trackFeedService.deleteFeed(id);
        return ResponseEntity.noContent().build();
    }
}
