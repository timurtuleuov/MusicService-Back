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
@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;
    @Autowired
    private ArtistService artistService;

    // Получение всех альбомов
    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {

        List<Album> albums = albumService.getAllAlbums();
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    // Получение альбома по ID
    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long id) {
        Optional<Album> album = albumService.getAlbumById(id);
        return album.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Создание нового альбома
    @PostMapping
    public ResponseEntity<Album> createAlbum(@RequestParam("artistId") Long artistId,
                                             @RequestParam("name") String name,
                                             @RequestParam("cover") MultipartFile cover) {
        Optional<Artist> artistOptional = artistService.getArtistById(artistId);

        if (artistOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Artist artist = artistOptional.get();

        // Преобразование MultipartFile в массив байтов (byte[])
        byte[] coverBytes;
        try {
            coverBytes = cover.getBytes();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Album album = new Album();
        album.setName(name);
        album.setCover(coverBytes);
        album.setArtist(artist);

        Album createdAlbum = albumService.createAlbum(album);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlbum);
    }


    // Обновление альбома
    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, @RequestBody Album updatedAlbum) {
        Album album = albumService.updateAlbum(id, updatedAlbum);
        if (album != null) {
            return new ResponseEntity<>(album, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удаление альбома по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        boolean deleted = albumService.deleteAlbum(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
