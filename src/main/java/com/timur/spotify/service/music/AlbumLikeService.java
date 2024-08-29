package com.timur.spotify.service.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.*;
import com.timur.spotify.repository.music.AlbumLikeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlbumLikeService {
    private final AlbumLikeRepository albumLikeRepository;

    public AlbumLikeService(AlbumLikeRepository albumLikeRepository) {
        this.albumLikeRepository = albumLikeRepository;
    }

    public Like likeAlbum(User user, Album album) {
        // Проверяем, поставил ли пользователь уже лайк этому треку
        Optional<Like> existingLike = albumLikeRepository.findByUserAndAlbum(user, album);
        if (existingLike.isPresent()) {
            return existingLike.get();
        }

        // Если нет, создаем новый лайк
        AlbumLike like = new AlbumLike(user, album);
        return albumLikeRepository.save(like);
    }
    public void unlikeAlbum(User user, Album album) {
        albumLikeRepository.deleteByUserAndAlbum(user, album);
    }

    public long countLikes(Album album) {
        return albumLikeRepository.countByAlbum(album);
    }
}
