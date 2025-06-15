package com.timur.spotify.service.music;

import com.timur.spotify.dto.PlaylistDTO;
import com.timur.spotify.dto.PlaylistShortDTO;
import com.timur.spotify.dto.PlaylistTrackDTO;
import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.entity.music.PlaylistTrack;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.repository.auth.UserRepository;
import com.timur.spotify.repository.music.PlaylistRepository;
import com.timur.spotify.repository.music.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final PlaylistTrackService playlistTrackService;

    public PlaylistService(PlaylistRepository playlistRepository, UserRepository userRepository, TrackRepository trackRepository, PlaylistTrackService playlistTrackService) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
        this.playlistTrackService = playlistTrackService;
    }

    @Transactional
    public PlaylistDTO createPlaylist(Playlist playlist) {
        if (playlist.getUser() == null || playlist.getUser().getId() == null) {
            throw new IllegalArgumentException("User must be specified");
        }

        User user = userRepository.findById(playlist.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + playlist.getUser().getId()));
        playlist.setUser(user);

        List<PlaylistTrack> processedPlaylistTracks = new ArrayList<>();

        if (playlist.getPlaylistTracks() != null && !playlist.getPlaylistTracks().isEmpty()) {
            for (PlaylistTrack playlistTrack : playlist.getPlaylistTracks()) {

                Track track = trackRepository.findById(playlistTrack.getTrack().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Track not found with id: " + playlistTrack.getTrack().getId()));
                PlaylistTrack newPlaylistTrack = new PlaylistTrack();
                newPlaylistTrack.setPlaylist(playlist);
                newPlaylistTrack.setTrack(track);
                newPlaylistTrack.setOrderInPlaylist(playlistTrack.getOrderInPlaylist());
                newPlaylistTrack.setAddedAt(playlistTrack.getAddedAt() != null ? playlistTrack.getAddedAt() : LocalDateTime.now());
                processedPlaylistTracks.add(newPlaylistTrack);
            }
            playlist.setPlaylistTracks(processedPlaylistTracks);
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);


        // Конвертируем в DTO
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setId(savedPlaylist.getId());
        playlistDTO.setName(savedPlaylist.getName());
        playlistDTO.setCover(savedPlaylist.getCover());
        playlistDTO.setPrivate(savedPlaylist.isPrivate());
        playlistDTO.setName(savedPlaylist.getUser().getUsername()); // Исправлено: setUsername, а не setName

        if (savedPlaylist.getPlaylistTracks() != null) {
            List<PlaylistTrackDTO> trackDTOs = savedPlaylist.getPlaylistTracks().stream().map(pt -> {
                PlaylistTrackDTO dto = new PlaylistTrackDTO();
                dto.setId(pt.getId());
                dto.setPlaylistId(pt.getPlaylist().getId());
                dto.setTrackId(pt.getTrack().getId());
                dto.setOrderInPlaylist(pt.getOrderInPlaylist());
                dto.setAddedAt(pt.getAddedAt());
                return dto;
            }).collect(Collectors.toList());
            playlistDTO.setPlaylistTrackList(trackDTOs);
        }

        return playlistDTO;
    }

    @Transactional
    public PlaylistDTO getById(Long id) {
        Optional<Playlist> playlist = playlistRepository.findById(id);
        return playlist.map(this::convertToDTO).orElse(null);
    }

    @Transactional
    public List<PlaylistShortDTO> getAllPlaylistByAuthorId(Long authorId) {
        List<Playlist> playlists = playlistRepository.findByUserId(authorId);
        return playlists.stream()
                .map(this::convertToShortDTO)
                .collect(Collectors.toList());
    }

    // Вспомогательный метод для преобразования Playlist в PlaylistDTO
    private PlaylistDTO convertToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setCover(playlist.getCover());
        dto.setPrivate(playlist.isPrivate());
        dto.setUsername(playlist.getUser() != null ? playlist.getUser().getUsername() : null);

        // Преобразование списка PlaylistTrack в PlaylistTrackDTO
        if (playlist.getPlaylistTracks() != null) {
            List<PlaylistTrackDTO> trackDTOs = playlist.getPlaylistTracks().stream().map(this::convertToTrackDTO).collect(Collectors.toList());
            dto.setPlaylistTrackList(trackDTOs);
        } else {
            dto.setPlaylistTrackList(new ArrayList<>());
        }

        return dto;
    }

    // Вспомогательный метод для преобразования PlaylistTrack в PlaylistTrackDTO
    private PlaylistTrackDTO convertToTrackDTO(PlaylistTrack playlistTrack) {
        PlaylistTrackDTO dto = new PlaylistTrackDTO();
        dto.setId(playlistTrack.getId());
        dto.setPlaylistId(playlistTrack.getPlaylist() != null ? playlistTrack.getPlaylist().getId() : null);
        dto.setTrackId(playlistTrack.getTrack() != null ? playlistTrack.getTrack().getId() : null);
        dto.setOrderInPlaylist(playlistTrack.getOrderInPlaylist());
        dto.setAddedAt(playlistTrack.getAddedAt());
        return dto;
    }

    private PlaylistShortDTO convertToShortDTO(Playlist playlist) {
        PlaylistShortDTO dto = new PlaylistShortDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setCover(playlist.getCover());
        dto.setPrivate(playlist.isPrivate());
        dto.setUsername(playlist.getUser() != null ? playlist.getUser().getUsername() : null);
        return dto;
    }
    @Transactional
    public Playlist updatePlaylist(Long id, Playlist playlist) {
        if (playlistRepository.existsById(id)) {
            playlist.setId(id);
            return playlistRepository.save(playlist);
        }
        return null;
    }

    public boolean deletePlaylist(Long id) {
        if (playlistRepository.existsById(id)) {
            playlistRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
