package com.timur.spotify.service.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Like;
import com.timur.spotify.entity.music.Track;

import com.timur.spotify.entity.music.TrackLike;
import com.timur.spotify.repository.music.TrackLikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrackLikeService {
    private final TrackLikeRepository likeRepository;

    public TrackLikeService(TrackLikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Like likeTrack(User user, Track track) {
        // Проверяем, поставил ли пользователь уже лайк этому треку
        Optional<Like> existingLike = likeRepository.findByUserAndTrack(user, track);
        if (existingLike.isPresent()) {
            return existingLike.get();
        }

        // Если нет, создаем новый лайк
        TrackLike like = new TrackLike(user, track);
        return likeRepository.save(like);
    }

    public List<Track> getLikedTracks(Long userId) {
        List<TrackLike> trackLikes = likeRepository.findByUserId(userId);
        return trackLikes.stream()
                .map(TrackLike::getTrack)
                .collect(Collectors.toList());
    }
    public void unlikeTrack(User user, Track track) {
        likeRepository.deleteByUserAndTrack(user, track);
    }

    public long countLikes(Track track) {
        return likeRepository.countByTrack(track);
    }
}
