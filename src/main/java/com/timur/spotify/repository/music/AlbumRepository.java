package com.timur.spotify.repository.music;

import com.timur.spotify.entity.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAllByArtistId(Long artistId);
}
