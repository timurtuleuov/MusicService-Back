package com.timur.spotify.repository.auth;

import com.timur.spotify.entity.auth.UserMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMetaRepository extends JpaRepository<UserMeta, Long> {
    Optional<UserMeta> findByUserId(Long userId);
}
