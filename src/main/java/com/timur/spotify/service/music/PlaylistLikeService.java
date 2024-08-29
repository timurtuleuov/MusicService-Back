package com.timur.spotify.service.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Album;
import com.timur.spotify.entity.music.AlbumLike;
import com.timur.spotify.entity.music.Like;
import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.entity.music.PlaylistLike;
import com.timur.spotify.repository.music.PlaylistLikeRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaylistLikeService {
    private final PlaylistLikeRepository likeRepository;

    public PlaylistLikeService(PlaylistLikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Like likePlaylist(User user, Playlist playlist) {
        // Проверяем, поставил ли пользователь уже лайк этому треку
        Optional<Like> existingLike = likeRepository.findByUserAndPlaylist(user, playlist);
        if (existingLike.isPresent()) {
            return existingLike.get();
        }

        // Если нет, создаем новый лайк
        PlaylistLike like = new PlaylistLike(user, playlist);
        return likeRepository.save(like);
    }
    public void unlikePlaylist(User user, Playlist playlist) {
        likeRepository.deleteByUserAndPlaylist(user, playlist);
    }

    public long countLikes(Playlist playlist) {
        return likeRepository.countByPlaylist(playlist);
    }
}
