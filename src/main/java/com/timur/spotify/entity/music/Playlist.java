package com.timur.spotify.entity.music;

import com.timur.spotify.entity.auth.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String cover;
    private boolean isPrivate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
