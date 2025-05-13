package com.timur.spotify.dto;

import lombok.Data;

@Data
public class PlaylistDTO {
    private Long id;
    private String name;
    private String cover;
    private boolean isPrivate;
    private String username;
}
