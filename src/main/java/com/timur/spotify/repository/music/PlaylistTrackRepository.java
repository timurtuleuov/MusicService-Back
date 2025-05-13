package com.timur.spotify.repository.music;

import com.timur.spotify.entity.music.PlaylistTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {

    List<PlaylistTrack> findByPlaylistId(Long playlistId);
    Optional<PlaylistTrack> findByPlaylistIdAndTrackId(Long playlistId, Long trackId);
}
