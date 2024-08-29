package com.timur.spotify.repository.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Like;
import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.entity.music.PlaylistLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, Long> {
    Optional<Like> findByUserAndPlaylist(User user, Playlist playlist);
    long countByPlaylist(Playlist playlist);
    void deleteByUserAndPlaylist(User user, Playlist playlist);
}
