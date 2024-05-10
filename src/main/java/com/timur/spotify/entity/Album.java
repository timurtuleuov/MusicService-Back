package com.timur.spotify.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private byte[] cover;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;
}
