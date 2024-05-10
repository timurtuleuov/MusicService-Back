package com.timur.spotify.service;

import com.timur.spotify.entity.Track;
import com.timur.spotify.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrackService {
    @Autowired
    private TrackRepository trackRepository;

    public Track createTrack(Track track) {
        return trackRepository.save(track);
    }

    public Optional<Track> getTrackById(Long id) {
        return trackRepository.findById(id);
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    public Track updateTrack(Long id, Track updatedTrack) {
        if (trackRepository.existsById(id)) {
            updatedTrack.setId(id);
            return trackRepository.save(updatedTrack);
        }
        return null;
    }

    public boolean deleteTrack(Long id) {
        if (trackRepository.existsById(id)) {
            trackRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
