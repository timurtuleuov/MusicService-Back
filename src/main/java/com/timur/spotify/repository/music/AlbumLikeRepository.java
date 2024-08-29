package com.timur.spotify.repository.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Album;
import com.timur.spotify.entity.music.AlbumLike;
import com.timur.spotify.entity.music.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumLikeRepository extends JpaRepository<AlbumLike, Long> {
    Optional<Like> findByUserAndAlbum(User user, Album album);
    long countByAlbum(Album album);
    void deleteByUserAndAlbum(User user, Album album);
}
