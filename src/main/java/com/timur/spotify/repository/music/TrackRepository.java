package com.timur.spotify.repository.music;

import com.timur.spotify.entity.music.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByAlbum_Artist_Id(Long artistId);
}
