package com.timur.spotify.service.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Like;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.repository.music.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Like likeTrack(User user, Track track) {
        // Проверяем, поставил ли пользователь уже лайк этому треку
        Optional<Like> existingLike = likeRepository.findByUserAndTrack(user, track);
        if (existingLike.isPresent()) {
            return existingLike.get();
        }

        // Если нет, создаем новый лайк
        Like like = new Like(user, track);
        return likeRepository.save(like);
    }
    public void unlikeTrack(User user, Track track) {
        likeRepository.deleteByUserAndTrack(user, track);
    }

    public long countLikes(Track track) {
        return likeRepository.countByTrack(track);
    }
}
