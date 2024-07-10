package com.timur.spotify.service;

import com.timur.spotify.entity.Track;
import com.timur.spotify.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TrackService {
    @Autowired
    private TrackRepository trackRepository;

    public Track createTrack(Track track) {
        Track savedTrack = trackRepository.save(track);

        cacheTrack(savedTrack);
        return savedTrack;
    }

    @CachePut(value = "TrackService::getTrackById", key = "#track.id")
    private void cacheTrack(Track track) {}

    @Cacheable(value = "TrackService::getTrackById", key = "#id")
    public Track getTrackById(Long id) {
        return trackRepository.findById(id).orElse(null);
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

//    @Cacheable(value="tracks", key = "track")
//    public List<Track> getPopularTracks() {
//        try {
//            List<Track> popularTracks = Arrays.asList(getTrackById(1L).get(), getTrackById(2L).get());
//            return popularTracks;
//        } catch (Exception e) {
//            System.out.println(e);
//            return List.of();
//        }
//    }
    @CachePut(value = "TrackService::getTrackById", key = "#id")
    public Track updateTrack(Long id, Track updatedTrack) {
        if (trackRepository.existsById(id)) {
            updatedTrack.setId(id);
            return trackRepository.save(updatedTrack);
        }
        return null;
    }
    @CacheEvict(value = "TrackService::getTrackById", key = "#id")
    public boolean deleteTrack(Long id) {
        if (trackRepository.existsById(id)) {
            trackRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
