package com.timur.spotify.entity.music;

import com.timur.spotify.entity.auth.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("TRACK")
public class TrackLike extends Like{
    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    public TrackLike() {}
    
    public TrackLike(User user, Track track) {
        super(user);
        this.track = track;
    }
}
