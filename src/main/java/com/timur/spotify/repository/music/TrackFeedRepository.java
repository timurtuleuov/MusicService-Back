package com.timur.spotify.repository.music;

import com.timur.spotify.entity.music.TrackFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackFeedRepository extends JpaRepository<TrackFeed, Long> {
}
