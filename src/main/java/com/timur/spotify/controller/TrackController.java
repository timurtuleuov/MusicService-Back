package com.timur.spotify.controller;

import com.timur.spotify.entity.Album;
import com.timur.spotify.entity.GenreType;
import com.timur.spotify.entity.Track;
import com.timur.spotify.service.AlbumService;
import com.timur.spotify.service.FileStorageService;
import com.timur.spotify.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

    //  Контроллер для воспроизведения аудио
    @GetMapping("/audio/{fileName:.+}")
    public ResponseEntity<ByteArrayResource> getAudioFile(@PathVariable String fileName) throws IOException {
        File file = new File("D:\\IT\\SpotifyClone\\spotify\\src\\main\\resources\\static\\" + fileName);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        long fileLength = file.length();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename(fileName).build());
        headers.set("Accept-Ranges", "bytes");

        if (headers.containsKey(HttpHeaders.RANGE)) {
            String rangeHeader = headers.getFirst(HttpHeaders.RANGE);
            long rangeStart = 0;
            long rangeEnd = fileLength - 1;

            if (rangeHeader != null) {
                String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                try {
                    rangeStart = Long.parseLong(ranges[0]);
                    if (ranges.length > 1) {
                        rangeEnd = Long.parseLong(ranges[1]);
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            }

            if (rangeStart > rangeEnd || rangeEnd >= fileLength) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                        .build();
            }

            long contentLength = rangeEnd - rangeStart + 1;
            headers.setContentLength(contentLength);
            headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);

            byte[] data = new byte[(int) contentLength];
            try (InputStream inputStream = new FileInputStream(file)) {
                inputStream.skip(rangeStart);

            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new ByteArrayResource(data));
        } else {
            byte[] data = Files.readAllBytes(file.toPath());
            headers.setContentLength(fileLength);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(data));
        }
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
