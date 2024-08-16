package com.timur.spotify.controller.music;
import com.timur.spotify.entity.music.Album;
import com.timur.spotify.entity.music.Artist;
import com.timur.spotify.service.music.AlbumService;
import com.timur.spotify.service.music.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/album")
public class AlbumController {
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    @Autowired
    private AlbumService albumService;
    @Autowired
    private ArtistService artistService;

    // Получение всех альбомов
    @GetMapping("/albums")
    public ResponseEntity<List<Album>> getAllAlbums() {
        logger.info("OPERATION: Getting all albums");
        List<Album> albums = albumService.getAllAlbums();
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    // Получение альбома по ID
    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long id) {
        Optional<Album> album = albumService.getAlbumById(id);
        logger.info("OPERATION: Getting album by id {}", id);
        if (!album.isEmpty()) {
            logger.info("SUCCESS: Album found");
        } else {
            logger.error("FAIL: Album doesn't found");
        }
        return album.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Создание нового альбома
    @PostMapping
    public ResponseEntity<Album> createAlbum(@RequestParam("artistId") Long artistId,
                                             @RequestParam("name") String name,
                                             @RequestParam("cover") MultipartFile cover) {

        logger.info("OPERATION: Creating album");

        Optional<Artist> artistOptional = artistService.getArtistById(artistId);

        if (artistOptional.isEmpty()) {
            logger.error("FAIL: Not found artist");
            return ResponseEntity.notFound().build();
        }

        Artist artist = artistOptional.get();

        // Преобразование MultipartFile в массив байтов (byte[])
        byte[] coverBytes;
        try {
            coverBytes = cover.getBytes();
        } catch (IOException e) {
            logger.error("ERROR: Couldn't transform cover into bytes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Album album = new Album();
        album.setName(name);
        album.setCover(coverBytes);
        album.setArtist(artist);

        Album createdAlbum = albumService.createAlbum(album);
        logger.info("SUCCESS: Created new album");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlbum);
    }


    // Обновление альбома
    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, @RequestBody Album updatedAlbum) {
        logger.info("OPERATION: Updating album with id {}", id);
        Album album = albumService.updateAlbum(id, updatedAlbum);
        if (album != null) {
            logger.info("SUCCESS: Updated album");
            return new ResponseEntity<>(album, HttpStatus.OK);
        } else {
            logger.error("ERROR: Album not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удаление альбома по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long id) {
        logger.info("OPERATION: Deleting album");
        boolean deleted = albumService.deleteAlbum(id);
        if (deleted) {
            logger.info("SUCCESS: Deleted album by id {}", id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Album deleted successfully. Response code: " + HttpStatus.NO_CONTENT.value());
        } else {
            logger.error("ERROR: Album not found by id {}", id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Album not found. Response code: " + HttpStatus.NOT_FOUND.value());
        }
    }
}
