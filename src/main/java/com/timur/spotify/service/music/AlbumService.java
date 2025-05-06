package com.timur.spotify.service.music;

import com.timur.spotify.entity.music.Album;
import com.timur.spotify.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepository albumRepository;

    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    public Optional<Album> getAlbumById(Long id) {
        return albumRepository.findById(id);
    }

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public Album updateAlbum(Long id, Album updatedAlbum) {
        if (albumRepository.existsById(id)) {
            updatedAlbum.setId(id);
            return albumRepository.save(updatedAlbum);
        }
        return null;
    }

    public boolean deleteAlbum(Long id) {
        if (albumRepository.existsById(id)) {
            albumRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Album> getAllAlbumByArtist(Long artistId) {
        return albumRepository.findAllByArtistId(artistId);
    }
}
