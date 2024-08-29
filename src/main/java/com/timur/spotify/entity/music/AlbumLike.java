package com.timur.spotify.entity.music;

import com.timur.spotify.entity.auth.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ALBUM")
public class AlbumLike extends Like {

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    AlbumLike(){}

    public AlbumLike(User user, Album album) {
        super(user);
        this. album = album;
    }
}
