package com.timur.spotify.repository.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.AlbumLike;
import com.timur.spotify.entity.music.Like;
import com.timur.spotify.entity.music.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumLikeRepository extends JpaRepository<AlbumLike, Long> {
    Optional<Like> findByUserAndTrack(User user, Track track);
    long countByTrack(Track post);
    void deleteByUserAndTrack(User user, Track track);
}
