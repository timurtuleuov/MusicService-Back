package com.timur.spotify.entity.music;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Track implements Serializable {
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
    private Integer duration;
}
