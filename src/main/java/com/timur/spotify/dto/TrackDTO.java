package com.timur.spotify.dto;

import com.timur.spotify.entity.music.Album;
import lombok.Data;

@Data
public class TrackDTO {
    private Long id;
    private String name;
    private String genre; // Предполагаем, что GenreType — это enum, преобразуем в String
    private String audioPath;
    private Album album; // Вложенный DTO для альбома
    private boolean liked; // Лайкнул ли пользователь
    private Integer duration;
}
