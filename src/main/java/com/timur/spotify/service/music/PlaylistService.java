package com.timur.spotify.service.music;

import com.timur.spotify.dto.PlaylistDTO;
import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.repository.music.PlaylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist getById(Long id) {
        Optional<Playlist> playlist = playlistRepository.findById(id);
        return playlist.orElse(null);
    }



//    Возможно, надо будет переделать для оптимизации приложения
public List<PlaylistDTO> getAllPlaylistByAuthorId(Long authorId) {
    List<Playlist> playlists = playlistRepository.findByUserId(authorId);
    return playlists.stream().map(playlist -> {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setCover(playlist.getCover());
        dto.setPrivate(playlist.isPrivate());
        dto.setUserId(playlist.getUser() != null ? playlist.getUser().getId() : null);
        return dto;
    }).collect(Collectors.toList());
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
