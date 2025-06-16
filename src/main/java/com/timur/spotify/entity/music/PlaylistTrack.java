package com.timur.spotify.entity.music;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
public class PlaylistTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    @JsonBackReference
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    private Integer orderInPlaylist;

    private LocalDateTime addedAt = LocalDateTime.now();
}
