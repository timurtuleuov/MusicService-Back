package com.timur.spotify.entity.music;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class TrackLike extends Like{
    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;
}
