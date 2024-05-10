package com.timur.spotify.entity;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Enumerated(EnumType.STRING)
    private GenreType genre;
    @Lob
    private String audioPath;
    @ManyToOne
    @JoinTable(
            name = "track_album",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private Album album;
}
