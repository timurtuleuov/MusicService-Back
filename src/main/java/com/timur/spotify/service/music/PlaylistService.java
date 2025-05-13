package com.timur.spotify.service.music;

import com.timur.spotify.dto.PlaylistDTO;
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
    public Playlist createPlaylist(Playlist playlist) {
        // Убедимся, что пользователь существует
        if (playlist.getUser() == null || playlist.getUser().getId() == null) {
            throw new IllegalArgumentException("User must be specified");
        }
        User user = userRepository.findById(playlist.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + playlist.getUser().getId()));
        playlist.setUser(user);

        // Создаем новый список для обработанных PlaylistTrack
        List<PlaylistTrack> processedPlaylistTracks = new ArrayList<>();

        // Обрабатываем playlistTracks
        if (playlist.getPlaylistTracks() != null && !playlist.getPlaylistTracks().isEmpty()) {
            for (PlaylistTrack playlistTrack : playlist.getPlaylistTracks()) {
                // Проверяем, что трек существует
                Track track = trackRepository.findById(playlistTrack.getTrack().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Track not found with id: " + playlistTrack.getTrack().getId()));

                // Создаем новый объект PlaylistTrack
                PlaylistTrack newPlaylistTrack = new PlaylistTrack();
                newPlaylistTrack.setPlaylist(playlist); // Устанавливаем связь с плейлистом
                newPlaylistTrack.setTrack(track);
                newPlaylistTrack.setOrderInPlaylist(playlistTrack.getOrderInPlaylist());
                newPlaylistTrack.setAddedAt(playlistTrack.getAddedAt() != null ? playlistTrack.getAddedAt() : LocalDateTime.now());
                processedPlaylistTracks.add(newPlaylistTrack); // Добавляем в новый список
            }
            // Устанавливаем обработанные треки в плейлист
            playlist.setPlaylistTracks(processedPlaylistTracks);
        }

        // Сохраняем плейлист с каскадированием
        Playlist savedPlaylist = playlistRepository.save(playlist);

        // Возвращаем обновленный плейлист
        return playlistRepository.findById(savedPlaylist.getId())
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve saved playlist"));
    }

    @Transactional
    public PlaylistDTO getById(Long id) {
        Optional<Playlist> playlist = playlistRepository.findById(id);
        return playlist.map(this::convertToDTO).orElse(null);
    }

    @Transactional
    public List<PlaylistDTO> getAllPlaylistByAuthorId(Long authorId) {
        List<Playlist> playlists = playlistRepository.findByUserId(authorId);
        return playlists.stream()
                .map(this::convertToDTO)
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
        dto.setPlaylistTrackList(playlist.getPlaylistTracks());
        return dto;
    }

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
