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
@DiscriminatorValue("PLAYLIST")
public class PlaylistLike extends Like{

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    PlaylistLike(){}

    public PlaylistLike(User user, Playlist playlist){
        super(user);
        this.playlist = playlist;
    }
}
