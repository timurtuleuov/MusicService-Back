package com.timur.spotify.service.music;

import com.timur.spotify.dto.TrackDTO;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.entity.music.TrackLike;
import com.timur.spotify.repository.music.TrackLikeRepository;
import com.timur.spotify.repository.music.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrackService {
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private TrackLikeRepository likeRepository;

    public Track createTrack(Track track) {
        Track savedTrack = trackRepository.save(track);

//        cacheTrack(savedTrack);
        return savedTrack;
    }

//    @CachePut(value = "TrackService::getTrackById", key = "#track.id")
    private void cacheTrack(Track track) {}

//    @Cacheable(value = "TrackService::getTrackById", key = "#id")
    public Track getTrackById(Long id) {
        return trackRepository.findById(id).orElse(null);
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

//    @Cacheable(value="tracks", key = "track")
//    public List<Track> getPopularTracks() {
//        try {
//            List<Track> popularTracks = Arrays.asList(getTrackById(1L).get(), getTrackById(2L).get());
//            return popularTracks;
//        } catch (Exception e) {
//            System.out.println(e);
//            return List.of();
//        }
//    }
//    @CachePut(value = "TrackService::getTrackById", key = "#id")
    public Track updateTrack(Long id, Track updatedTrack) {
        if (trackRepository.existsById(id)) {
            updatedTrack.setId(id);
            return trackRepository.save(updatedTrack);
        }
        return null;
    }
//    @CacheEvict(value = "TrackService::getTrackById", key = "#id")
    public boolean deleteTrack(Long id) {
        if (trackRepository.existsById(id)) {
            trackRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TrackDTO> getTrackFeed(Long userId) {
        List<Track> tracks = trackRepository.findAll(); // Получаем все треки для ленты
        List<TrackLike> userLikes = likeRepository.findByUserId(userId);

        // Создаём список ID лайкнутых треков
        Set<Long> likedTrackIds = userLikes.stream()
                .map(trackLike -> trackLike.getTrack().getId())
                .collect(Collectors.toSet());

        // Преобразуем треки в DTO с информацией о лайках
        return tracks.stream()
                .map(track -> {
                    TrackDTO dto = new TrackDTO();
                    dto.setId(track.getId());
                    dto.setName(track.getName());
                    dto.setGenre(track.getGenre().name());
                    dto.setAudioPath(track.getAudioPath());
                    dto.setAlbum(track.getAlbum()); // Предполагается, что Album — это объект
                    dto.setLiked(likedTrackIds.contains(track.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TrackDTO> getTracksByIDs(Long[] idArray, Long userId) {
        // Преобразуем массив в список
        List<Long> ids = Arrays.asList(idArray);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // Загружаем треки с помощью JOIN FETCH
        List<Track> tracks = trackRepository.findByIds(ids);

        // Загружаем лайки пользователя для указанных треков
        List<TrackLike> trackLikes = likeRepository.findAllByIdIn(idArray);
        Set<Long> likedTrackIds = trackLikes.stream()
                .map(trackLike -> trackLike.getTrack().getId())
                .collect(Collectors.toSet());

        // Сортируем треки в соответствии с порядком idArray
        Map<Long, Track> trackMap = tracks.stream()
                .collect(Collectors.toMap(Track::getId, track -> track));
        List<Track> sortedTracks = ids.stream()
                .map(trackMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Преобразуем в DTO
        return sortedTracks.stream()
                .map(track -> {
                    TrackDTO dto = new TrackDTO();
                    dto.setId(track.getId());
                    dto.setName(track.getName());
                    dto.setGenre(track.getGenre().name());
                    dto.setAudioPath(track.getAudioPath());
                    dto.setDuration(track.getDuration());
                    dto.setAlbum(track.getAlbum()); // Album уже загружен через JOIN FETCH
                    dto.setLiked(likedTrackIds.contains(track.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public List<Track> getTracksByArtist(Long artistId) {
        List<Track> tracksByArtist = trackRepository.findByAlbum_Artist_Id(artistId);
        return tracksByArtist;
    }
    @Transactional
    public List<TrackDTO> getTracksByArtist(Long artistId, Long userId) {
        List<Track> tracksByArtist = getTracksByArtist(artistId);
        List<TrackLike> userLikes = likeRepository.findByUserId(userId);

        Set<Long> likedTrackIds = userLikes.stream()
                .map(trackLike -> trackLike.getTrack().getId())
                .collect(Collectors.toSet());

        // Преобразуем треки в DTO с информацией о лайках
        return tracksByArtist.stream()
                .map(track -> {
                    TrackDTO dto = new TrackDTO();
                    dto.setId(track.getId());
                    dto.setName(track.getName());
                    dto.setGenre(track.getGenre().name());
                    dto.setAudioPath(track.getAudioPath());
                    dto.setDuration(track.getDuration());
                    dto.setAlbum(track.getAlbum()); // Предполагается, что Album — это объект
                    dto.setLiked(likedTrackIds.contains(track.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TrackDTO> getTracksByAlbum(Long albumId, Long userId) {
        List<Track> tracksByAlbum = trackRepository.findByAlbum_Id(albumId);

        List<TrackLike> trackLikes = likeRepository.findByUserId(userId);

        Set<Long> likedTrackIds = trackLikes.stream()
                .map(trackLike -> trackLike.getTrack().getId())
                .collect(Collectors.toSet());
        return tracksByAlbum.stream()
                .map(track -> {
                    TrackDTO dto = new TrackDTO();
                    dto.setId(track.getId());
                    dto.setName(track.getName());
                    dto.setGenre(track.getGenre().name());
                    dto.setAudioPath(track.getAudioPath());
                    dto.setDuration(track.getDuration());
                    dto.setAlbum(track.getAlbum()); // Предполагается, что Album — это объект
                    dto.setLiked(likedTrackIds.contains(track.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TrackDTO> searchTracks(String query, Long userId) {
        List<Track> tracks =  trackRepository.findByNameContainingIgnoreCaseOrAlbum_Artist_NameContainingIgnoreCase(query, query);
        List<TrackLike> userLikes = likeRepository.findByUserId(userId);
        Set<Long> likedTrackIds = userLikes.stream()
                .map(trackLike -> trackLike.getTrack().getId())
                .collect(Collectors.toSet());

        // Преобразуем треки в DTO с информацией о лайках
        return tracks.stream()
                .map(track -> {
                    TrackDTO dto = new TrackDTO();
                    dto.setId(track.getId());
                    dto.setName(track.getName());
                    dto.setGenre(track.getGenre().name());
                    dto.setAudioPath(track.getAudioPath());
                    dto.setDuration(track.getDuration());
                    dto.setAlbum(track.getAlbum()); // Предполагается, что Album — это объект
                    dto.setLiked(likedTrackIds.contains(track.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
