package com.timur.spotify.entity.music;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Album implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private byte[] cover;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;
}
