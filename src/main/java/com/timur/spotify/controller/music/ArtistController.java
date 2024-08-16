package com.timur.spotify.controller.music;

import com.timur.spotify.entity.music.Artist;
import com.timur.spotify.service.music.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/artist")
public class ArtistController {
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    @Autowired
    private ArtistService artistService;

    // Получение всех исполнителей
    @GetMapping("/artists")
    public ResponseEntity<List<Artist>> getAllArtists() {
        List<Artist> artists = artistService.getAllArtists();
        logger.info("OPERATION: Getting all artists");
        return new ResponseEntity<>(artists, HttpStatus.OK);
    }

    // Получение исполнителя по ID
    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id) {
        logger.info("OPERATION: Getting artist by id {}", id);
        Optional<Artist> artist = artistService.getArtistById(id);
        if (!artist.isEmpty()){
            logger.info("SUCCESS: Artist found");
        } else {
            logger.info("FAIL: Artist not found");
        }
        return artist.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Создание нового исполнителя
    @PostMapping
    public ResponseEntity<Artist> createArtist(@RequestParam("name") String name,
                                               @RequestParam("avatar") MultipartFile avatar) throws IOException {
        logger.info("OPERATION: Creating artist with name {}", name);
        Artist artist = new Artist();
        artist.setName(name);
        artist.setAvatar(avatar.getBytes());
        Artist createdArtist = artistService.createArtist(artist);
        return new ResponseEntity<>(createdArtist, HttpStatus.CREATED);
    }

    // Обновление исполнителя
    @PutMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable Long id, @RequestBody Artist updatedArtist) {
        logger.info("OPERATION: Updating artist with id {}", id);
        Artist artist = artistService.updateArtist(id, updatedArtist);
        if (artist != null) {
            logger.info("SUCCESS: Updated artist");
            return new ResponseEntity<>(artist, HttpStatus.OK);
        } else {
            logger.error("ERROR: Artist not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удаление исполнителя по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        logger.info("OPERATION: Deleting album");
        boolean deleted = artistService.deleteArtist(id);
        if (deleted) {
            logger.info("SUCCESS: Deleted artist by id {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.error("ERROR: Artist not found by id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
