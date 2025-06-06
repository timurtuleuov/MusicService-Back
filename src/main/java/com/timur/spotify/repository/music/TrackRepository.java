package com.timur.spotify.repository.music;

import com.timur.spotify.entity.music.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByAlbum_Artist_Id(Long artistId);
    List<Track> findByNameContainingIgnoreCaseOrAlbum_Artist_NameContainingIgnoreCase(String name, String artistName);
    List<Track> findByAlbum_Id(Long albumId);
    @Query("SELECT t FROM Track t " +
            "WHERE t.id IN :ids")
    List<Track> findByIds(@Param("ids") List<Long> ids);
}
