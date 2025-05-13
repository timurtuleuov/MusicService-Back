package com.timur.spotify.service.music;

import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.entity.music.PlaylistTrack;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.repository.music.PlaylistRepository;
import com.timur.spotify.repository.music.PlaylistTrackRepository;
import com.timur.spotify.repository.music.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistTrackService {

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    /**
     * Получение всех связей PlaylistTrack для указанного плейлиста
     * @param playlistId ID плейлиста
     * @return Список PlaylistTrack
     */
    public List<PlaylistTrack> getAllByPlaylistId(Long playlistId) {
        return playlistTrackRepository.findByPlaylistId(playlistId);
    }

    /**
     * Добавление трека в плейлист
     * @param playlistId ID плейлиста
     * @param trackId ID трека
     * @param orderInPlaylist Порядок трека в плейлисте
     * @return Сохраненная сущность PlaylistTrack
     * @throws IllegalArgumentException Если плейлист или трек не найдены
     */
    @Transactional
    public PlaylistTrack addTrackToPlaylist(Long playlistId, Long trackId, Integer orderInPlaylist) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found with id: " + playlistId));
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found with id: " + trackId));

        PlaylistTrack playlistTrack = new PlaylistTrack();
        playlistTrack.setPlaylist(playlist); // Устанавливаем playlist
        playlistTrack.setTrack(track);       // Устанавливаем track
        playlistTrack.setOrderInPlaylist(orderInPlaylist != null ? orderInPlaylist : getNextOrder(playlistId));
        playlistTrack.setAddedAt(LocalDateTime.now());

        return playlistTrackRepository.save(playlistTrack);
    }

    /**
     * Удаление трека из плейлиста
     * @param playlistId ID плейлиста
     * @param trackId ID трека
     * @throws IllegalArgumentException Если связь не найдена
     */
    @Transactional
    public void removeTrackFromPlaylist(Long playlistId, Long trackId) {
        PlaylistTrack playlistTrack = playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found in playlist with id: " + playlistId));
        playlistTrackRepository.delete(playlistTrack);
    }

    /**
     * Обновление порядка трека в плейлисте
     * @param playlistTrackId ID записи PlaylistTrack
     * @param newOrder Новый порядок
     * @return Обновленная сущность PlaylistTrack
     * @throws IllegalArgumentException Если запись не найдена
     */
    @Transactional
    public PlaylistTrack updateOrder(Long playlistTrackId, Integer newOrder) {
        PlaylistTrack playlistTrack = playlistTrackRepository.findById(playlistTrackId)
                .orElseThrow(() -> new IllegalArgumentException("PlaylistTrack not found with id: " + playlistTrackId));
        playlistTrack.setOrderInPlaylist(newOrder);
        return playlistTrackRepository.save(playlistTrack);
    }

    /**
     * Получение записи PlaylistTrack по ID
     * @param id ID записи
     * @return PlaylistTrack или null, если не найдена
     */
    public PlaylistTrack getById(Long id) {
        Optional<PlaylistTrack> playlistTrack = playlistTrackRepository.findById(id);
        return playlistTrack.orElse(null);
    }

    /**
     * Вспомогательный метод для получения следующего порядка в плейлисте
     * @param playlistId ID плейлиста
     * @return Следующий порядок (максимальный + 1)
     */
    private Integer getNextOrder(Long playlistId) {
        List<PlaylistTrack> tracks = playlistTrackRepository.findByPlaylistId(playlistId);
        return tracks.stream()
                .mapToInt(PlaylistTrack::getOrderInPlaylist)
                .max()
                .orElse(0) + 1;
    }
}