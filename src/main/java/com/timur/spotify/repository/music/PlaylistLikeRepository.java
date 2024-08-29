package com.timur.spotify.repository.music;

import com.timur.spotify.entity.music.PlaylistLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, Long> {
}
