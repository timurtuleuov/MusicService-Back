package com.timur.spotify.entity.auth;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] avatar;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    private boolean showPlaylist = true;
    private boolean showProfile = true;
}
