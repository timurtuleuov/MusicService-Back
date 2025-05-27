package com.timur.spotify.dto;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class UserMetaDTO {
    private Long id;
    private String username;
    private String email;

    private String avatar;
    private boolean showPlaylist = true;
    private boolean showProfile = true;
}
