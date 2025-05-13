package com.timur.spotify.dto;

import com.timur.spotify.entity.music.PlaylistTrack;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistDTO {
    private Long id;
    private String name;
    private String cover;
    private boolean isPrivate;
    private String username;
    private List<PlaylistTrack> playlistTrackList;
}
