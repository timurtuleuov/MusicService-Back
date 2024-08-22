package com.timur.spotify.service.music;

import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.repository.music.PlaylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Playlist> getAllPlaylistByAuthorId(Long authorId){
        return playlistRepository.findAll().stream().filter(p -> p.getAuthor().getId().equals(authorId)).collect(Collectors.toList());
    }

    public Playlist updatePlaylist(Long id, Playlist playlist) {
        if (playlistRepository.existsById(id)) {
            playlist.setId(id);
            return playlistRepository.save(playlist);
        }
        return null;
    }


}
