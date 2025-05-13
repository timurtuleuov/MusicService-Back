package com.timur.spotify.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PlaylistTrackDTO {
    private Long id;
    private Long playlistId; // ID плейлиста вместо полной сущности
    private Long trackId;    // ID трека вместо полной сущности
    private Integer orderInPlaylist;
    private LocalDateTime addedAt;
}
