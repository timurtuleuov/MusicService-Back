package com.timur.spotify.service.music;

import com.timur.spotify.dto.TrackDTO;
import com.timur.spotify.dto.TrackFeedDTO;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.entity.music.TrackFeed;
import com.timur.spotify.repository.music.TrackFeedRepository;
import com.timur.spotify.repository.music.TrackLikeRepository;
import com.timur.spotify.repository.music.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackFeedService {

    private final TrackFeedRepository trackFeedRepository;
    private final TrackRepository trackRepository;
    private final TrackLikeRepository likeRepository;

    public List<TrackFeedDTO> getAllFeeds() {
        return trackFeedRepository.findAll().stream()
                .map(this::mapToDTOWithoutLikes)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<TrackFeedDTO> getAllFeedsForUser(Long userId) {
        Set<Long> likedTrackIds = likeRepository.findByUserId(userId).stream()
                .map(trackLike -> trackLike.getTrack().getId())
                .collect(Collectors.toSet());

        return trackFeedRepository.findAll().stream()
                .map(feed -> mapToDTO(feed, likedTrackIds))
                .collect(Collectors.toList());
    }
    @Transactional
    public Optional<TrackFeedDTO> getFeedById(Long id) {
        return trackFeedRepository.findById(id)
                .map(this::mapToDTOWithoutLikes);
    }

    public TrackFeedDTO createFeed(TrackFeedDTO dto) {
        TrackFeed feed = new TrackFeed();
        feed.setTitle(dto.getTitle());
        feed.setDescription(dto.getDescription());

        List<Track> tracks = trackRepository.findAllById(
                dto.getTracks().stream().map(TrackDTO::getId).toList()
        );
        feed.setTracks(tracks);

        return mapToDTOWithoutLikes(trackFeedRepository.save(feed));
    }

    public TrackFeedDTO updateFeed(Long id, TrackFeedDTO dto) {
        TrackFeed feed = trackFeedRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found"));

        feed.setTitle(dto.getTitle());
        feed.setDescription(dto.getDescription());

        List<Track> tracks = trackRepository.findAllById(
                dto.getTracks().stream().map(TrackDTO::getId).toList()
        );
        feed.setTracks(tracks);

        return mapToDTOWithoutLikes(trackFeedRepository.save(feed));
    }

    public void deleteFeed(Long id) {
        trackFeedRepository.deleteById(id);
    }

    // === Mapping ===

    private TrackFeedDTO mapToDTO(TrackFeed feed, Set<Long> likedTrackIds) {
        TrackFeedDTO dto = new TrackFeedDTO();
        dto.setId(feed.getId());
        dto.setTitle(feed.getTitle());
        dto.setDescription(feed.getDescription());
        dto.setTracks(feed.getTracks().stream()
                .map(track -> mapTrackToDTO(track, likedTrackIds))
                .collect(Collectors.toList()));
        return dto;
    }

    private TrackFeedDTO mapToDTOWithoutLikes(TrackFeed feed) {
        TrackFeedDTO dto = new TrackFeedDTO();
        dto.setId(feed.getId());
        dto.setTitle(feed.getTitle());
        dto.setDescription(feed.getDescription());
        dto.setTracks(feed.getTracks().stream()
                .map(this::mapTrackToDTOWithoutLikes)
                .collect(Collectors.toList()));
        return dto;
    }

    private TrackDTO mapTrackToDTO(Track track, Set<Long> likedTrackIds) {
        TrackDTO dto = new TrackDTO();
        dto.setId(track.getId());
        dto.setName(track.getName());
        dto.setGenre(track.getGenre().name());
        dto.setAudioPath(track.getAudioPath());
        dto.setAlbum(track.getAlbum()); // Можно заменить на AlbumDTO
        dto.setDuration(track.getDuration());
        dto.setLiked(likedTrackIds.contains(track.getId()));
        return dto;
    }

    private TrackDTO mapTrackToDTOWithoutLikes(Track track) {
        TrackDTO dto = new TrackDTO();
        dto.setId(track.getId());
        dto.setName(track.getName());
        dto.setGenre(track.getGenre().name());
        dto.setAudioPath(track.getAudioPath());
        dto.setAlbum(track.getAlbum());
        dto.setDuration(track.getDuration());
        dto.setLiked(false); // или null, по ситуации
        return dto;
    }
}
