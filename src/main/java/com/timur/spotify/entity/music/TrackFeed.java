package com.timur.spotify.entity.music;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

//@Data
//@Entity
//public class TrackFeed {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//    private String description;
//
//    @ManyToMany
//    private List<Track> tracks;
//}