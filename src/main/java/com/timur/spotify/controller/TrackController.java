package com.timur.spotify.controller;

import com.timur.spotify.entity.Album;
import com.timur.spotify.entity.GenreType;
import com.timur.spotify.entity.Track;
import com.timur.spotify.service.AlbumService;
import com.timur.spotify.service.FileStorageService;
import com.timur.spotify.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/track")
public class TrackController {
    @Autowired
    private TrackService trackService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<Track>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    // Получение трека по ID
    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        Optional<Track> track = trackService.getTrackById(id);
        return track.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Создание нового трека
    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestParam("name") String name, @RequestParam("albumId") Long albumId, @RequestParam("genre") String genre,
                                             @RequestParam("audio") MultipartFile audio) throws IOException {
        Optional<Album> albumOptional = albumService.getAlbumById(albumId);
        Album album = albumOptional.get();
        Track newTrack = new Track();
        newTrack.setAlbum(album);
        newTrack.setName(name);
        newTrack.setGenre(GenreType.valueOf(genre));
        String filePath = fileStorageService.saveFile(audio);
        newTrack.setAudioPath(filePath);
        Track createdTrack = trackService.createTrack(newTrack);
        return new ResponseEntity<>(createdTrack, HttpStatus.CREATED);
    }

    // Обновление трека
    @PutMapping("/{id}")
    public ResponseEntity<Track> updateTrack(@PathVariable Long id, @RequestBody Track updatedTrack) {
        Track track = trackService.updateTrack(id, updatedTrack);
        if (track != null) {
            return new ResponseEntity<>(track, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удаление трека по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        boolean deleted = trackService.deleteTrack(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}
