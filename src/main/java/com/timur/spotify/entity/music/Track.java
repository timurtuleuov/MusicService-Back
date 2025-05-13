package com.timur.spotify.entity.music;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Track implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private GenreType genre;

    @Lob
    @Basic(fetch = FetchType.LAZY) // Ленивая загрузка для LOB
    @JsonIgnore // Игнорируем при сериализации
    private String audioPath;

    @ManyToOne
    @JoinTable(
            name = "track_album",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private Album album;

    private Integer duration;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistTrack> playlistTracks = new ArrayList<>();
}
