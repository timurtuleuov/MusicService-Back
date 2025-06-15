package com.timur.spotify.controller.music;

import com.timur.spotify.dto.TrackDTO;
import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Album;
import com.timur.spotify.entity.music.GenreType;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.service.auth.UserService;
import com.timur.spotify.service.music.AlbumService;
import com.timur.spotify.service.music.FileStorageService;
import com.timur.spotify.service.music.TrackLikeService;
import com.timur.spotify.service.music.TrackService;
import jakarta.servlet.http.HttpServletRequest;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/track")
public class TrackController {
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    @Autowired
    private TrackService trackService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private TrackLikeService likeService;
    @GetMapping
    public ResponseEntity<List<Track>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    @GetMapping("/{userId}/all-tracks")
    public List<TrackDTO> getAllTracksByUser(@PathVariable Long userId) {
        return likeService.getAllTracksByUser(userId);
    }

    @GetMapping("/{userId}/liked-tracks")
    public List<TrackDTO> getLikedTracks(@PathVariable Long userId) {
        return likeService.getLikedTracks(userId);
    }

    @GetMapping("/tracks")
    public List<TrackDTO> getTrackFeed(@RequestParam("userId") Long userId) {
        return trackService.getTrackFeed(userId);
    }

    @GetMapping("/tracks-list")
    public List<TrackDTO> getTrackFeedList(@RequestParam("ids") Long[] idArray, @RequestParam("userId") Long userId) {
        logger.info("OPERATION: Getting tracks by ids  {}", Arrays.toString(idArray));
        return trackService.getTracksByIDs(idArray, userId);
    }

    @GetMapping("/artist/{artistId}")
    public List<TrackDTO> getTrackFeed(@PathVariable Long artistId, @RequestParam("userId") Long userId) {
        logger.info("OPERATION: Getting tracks of artist for user {}", userId);
        return trackService.getTracksByArtist(artistId, userId);
    }

    @GetMapping("/album/{albumId}")
    public List<TrackDTO> getTrackByAlbum(@PathVariable Long albumId, @RequestParam("userId") Long userId) {
        logger.info("OPERATION: Getting tracks from album  {}", albumId);
        return trackService.getTracksByAlbum(albumId, userId);
    }
    //  Контроллер для воспроизведения аудио
    @GetMapping("/audio/{fileName:.+}")
    public ResponseEntity<ByteArrayResource> getAudioFile(@PathVariable String fileName) throws IOException {
        logger.info("OPERATION: Getting audio of track by name {}", fileName);
<<<<<<< HEAD
        Path path = Paths.get("src/main/resources/static/" + fileName);
        File file = path.toFile();
=======

//        File file = new File("E:\\IT\\1SpotifyClone\\spotify\\src\\main\\resources\\static\\" + fileName);
        File file = new File("src/main/resources/static/" + fileName);
>>>>>>> b137176ded82a9976011ada03052c4c3831fa8ca
        if (!file.exists()) {
            logger.error("FAIL: Audio file with name {} doesn't exist", fileName);
            return ResponseEntity.notFound().build();
        }

        long fileLength = file.length();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename(fileName).build());
        headers.set("Accept-Ranges", "bytes");

        // Проверка Range-заголовка (приходит от клиента, например, браузера)
        String rangeHeader = RequestContextHolder.currentRequestAttributes()
                .getAttribute("org.springframework.web.context.request.ServletRequestAttributes.REQUEST", 0)
                instanceof HttpServletRequest req
                ? req.getHeader(HttpHeaders.RANGE)
                : null;

        if (rangeHeader != null) {
            long rangeStart = 0;
            long rangeEnd = fileLength - 1;

            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            try {
                rangeStart = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    rangeEnd = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                logger.error("FAIL: Failed to parse range header for file {}. Bad request format: {}", fileName, rangeHeader);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            if (rangeStart > rangeEnd || rangeEnd >= fileLength) {
                logger.error("FAIL: Invalid range request for file {}. Requested range: {}-{}, File length: {}",
                        fileName, rangeStart, rangeEnd, fileLength);
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                        .build();
            }

            long contentLength = rangeEnd - rangeStart + 1;
            headers.setContentLength(contentLength);
            headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);

            byte[] data = new byte[(int) contentLength];
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(rangeStart);
                raf.readFully(data);
            }

            logger.info("SUCCESS: Successfully processed partial content request for file {}", fileName);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new ByteArrayResource(data));
        } else {
            // Полный файл
            byte[] data = Files.readAllBytes(file.toPath());
            headers.setContentLength(fileLength);

            logger.info("SUCCESS: Successfully processed full content request for file {}", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(data));
        }
    }

    // Получение трека по ID
    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        logger.info("OPERATION: Getting track by id {}", id);
        Track track = trackService.getTrackById(id);
        if (track != null) {
            logger.info("SUCCESS: Track found");
            return new ResponseEntity<>(track, HttpStatus.OK);
        } else {
            logger.info("FAIL: Track not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




    // Создание нового трека
    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestParam("name") String name, @RequestParam("albumId")
                                             Long albumId, @RequestParam("genre") String genre,
                                             @RequestParam("audio") MultipartFile audio) throws IOException, UnsupportedAudioFileException {
        logger.info("OPERATION: Creating track with name {}, album ID {} and genre {}",
                name, albumId, genre);
        Optional<Album> albumOptional = albumService.getAlbumById(albumId);
        Album album = albumOptional.get();
        Track newTrack = new Track();
        newTrack.setAlbum(album);
        newTrack.setName(name);
        newTrack.setGenre(GenreType.valueOf(genre));

        // Сохранить аудиофайл
        String filePath = fileStorageService.saveFile(audio);
        newTrack.setAudioPath(filePath);

        // Извлечь длительность аудиофайла
        Integer durationInSeconds = 0; // Default value
        File tempFile = null;
        try {
            // Save MultipartFile to a temporary file
            tempFile = Files.createTempFile("track_", audio.getOriginalFilename()).toFile();
            audio.transferTo(tempFile);

            // Read audio metadata
            AudioFile audioMetadata = AudioFileIO.read(tempFile);
            durationInSeconds = audioMetadata.getAudioHeader().getTrackLength();
            logger.info("Calculated duration for track {}: {} seconds", name, durationInSeconds);
        } catch (CannotReadException | InvalidAudioFrameException | TagException | IOException e) {
            logger.warn("Error extracting duration for track " + name + ": " + e.getMessage() + ". File: " + audio.getOriginalFilename());
            // Continue with duration = 0
        } catch (ReadOnlyFileException e) {
            throw new RuntimeException(e);
        } finally {
            // Clean up temporary file
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    logger.warn("Failed to delete temporary file: " + e.getMessage());
                }
            }
        }

        newTrack.setDuration(durationInSeconds);

        // Сохранить трек
        Track createdTrack = trackService.createTrack(newTrack);
        logger.info("SUCCESS: Created track with name {}", name);

        return new ResponseEntity<>(createdTrack, HttpStatus.CREATED);
    }

    // Обновление трека
    @PutMapping("/{id}")
    public ResponseEntity<Track> updateTrack(@PathVariable Long id, @RequestBody Track updatedTrack) {
        logger.info("OPERATION: Updating track with id {}", id);
        Track track = trackService.updateTrack(id, updatedTrack);
        if (track != null) {
            logger.info("SUCCESS: Updated track");
            return new ResponseEntity<>(track, HttpStatus.OK);
        } else {
            logger.error("ERROR: Track not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удаление трека по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        logger.info("OPERATION: Deleting track");
        boolean deleted = trackService.deleteTrack(id);
        if (deleted) {
            logger.info("SUCCESS: Deleted track by id {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.error("ERROR: Track not found by id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RestController
    @RequestMapping("/tracks/{trackId}/likes")
    public static class TrackLikeController {

        private final TrackLikeService likeService;
        private final TrackService trackService;
        private final UserService userService;

        public TrackLikeController(TrackLikeService likeService, TrackService trackService, UserService userService) {
            this.likeService = likeService;
            this.trackService = trackService;
            this.userService = userService;
        }

        @PostMapping
        public ResponseEntity<Void> likeTrack(@PathVariable Long trackId, @AuthenticationPrincipal User user) {
            logger.info("OPERATION: like track {} by user {}", trackId, user.getId());
            Track track = trackService.getTrackById(trackId);
            likeService.likeTrack(user, track);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }


        @DeleteMapping
        public ResponseEntity<Void> unlikeTrack(@PathVariable Long trackId, @AuthenticationPrincipal User user) {
            logger.info("OPERATION: unlike track {} by user {}", trackId, user.getId());
            Track track = trackService.getTrackById(trackId);
            likeService.unlikeTrack(user, track);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        @GetMapping("/count")
        public ResponseEntity<Long> getLikeCount(@PathVariable Long trackId) {
            Track track = trackService.getTrackById(trackId);
            long likeCount = likeService.countLikes(track);
            return new ResponseEntity<>(likeCount, HttpStatus.OK);
        }

    }
}
