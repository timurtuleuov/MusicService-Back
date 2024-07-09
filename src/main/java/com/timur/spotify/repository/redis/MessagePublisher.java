package com.timur.spotify.repository.redis;

public interface MessagePublisher {
    void publish(final String message);
}
