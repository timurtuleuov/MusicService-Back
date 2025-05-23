package com.timur.spotify.service.music;

import com.timur.spotify.dto.TrackDTO;
import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Like;
import com.timur.spotify.entity.music.Track;

import com.timur.spotify.entity.music.TrackLike;
import com.timur.spotify.repository.music.TrackLikeRepository;
import com.timur.spotify.repository.music.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrackLikeService {
    private final TrackLikeRepository likeRepository;
    private final TrackRepository trackRepository;

    public TrackLikeService(TrackLikeRepository likeRepository, TrackRepository trackRepository) {
        this.likeRepository = likeRepository;
        this.trackRepository = trackRepository;
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

    @Transactional
    public List<TrackDTO> getAllTracksByUser(Long userId) {
        List<Track> tracks = trackRepository.findAll();
        List<TrackLike> trackLikes = likeRepository.findByUserId(userId);

        // Создаём множество ID треков, которые пользователь лайкнул, для быстрого поиска
        Set<Long> likedTrackIds = trackLikes.stream()
                .map(trackLike -> trackLike.getTrack().getId())
                .collect(Collectors.toSet());

        // Преобразуем треки в TrackDTO
        List<TrackDTO> tracksByUser = tracks.stream().map(track -> {
            TrackDTO trackDTO = new TrackDTO();
            trackDTO.setId(track.getId());
            trackDTO.setName(track.getName());
            trackDTO.setGenre(track.getGenre().name());
            trackDTO.setAudioPath(track.getAudioPath());
            trackDTO.setAlbum(track.getAlbum());
            trackDTO.setLiked(likedTrackIds.contains(track.getId()));
            return trackDTO;
        }).collect(Collectors.toList());

        return tracksByUser;
    }

    @Transactional
    public List<TrackDTO> getLikedTracks(Long userId) {
        List<TrackLike> trackLikes = likeRepository.findByUserId(userId);
        List<TrackDTO> tracksByUser = trackLikes.stream().map(track -> {
            TrackDTO trackDTO = new TrackDTO();
            trackDTO.setId(track.getId());
            trackDTO.setName(track.getTrack().getName());
            trackDTO.setGenre(track.getTrack().getGenre().name());
            trackDTO.setAudioPath(track.getTrack().getAudioPath());
            trackDTO.setAlbum(track.getTrack().getAlbum());
            trackDTO.setLiked(true);
            trackDTO.setDuration(track.getTrack().getDuration());
            return trackDTO;
        }).collect(Collectors.toList());
        return tracksByUser;
    }

    @Transactional
    public void unlikeTrack(User user, Track track) {
        likeRepository.deleteByUserAndTrack(user, track);
    }

    public long countLikes(Track track) {
        return likeRepository.countByTrack(track);
    }
}
