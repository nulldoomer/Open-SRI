// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.redis;

import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import io.github.opensri.playground_service.domain.port.SessionRepository;
import java.time.Duration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RedisSessionRepository implements SessionRepository {

  private static final String REDIS_KEY_PREFIX = "session:";
  private static final Duration SESSION_TTL = Duration.ofHours(1);

  private final ReactiveStringRedisTemplate redisTemplate;
  private final SessionMapper sessionMapper;

  public RedisSessionRepository(
      ReactiveStringRedisTemplate redisTemplate, SessionMapper sessionMapper) {
    this.redisTemplate = redisTemplate;
    this.sessionMapper = sessionMapper;
  }

  @Override
  public Mono<Void> save(PlaygroundSession session) {
    String key = buildKey(session.id());
    var map = sessionMapper.toMap(session);

    return redisTemplate
        .opsForHash()
        .putAll(key, map)
        .then(redisTemplate.expire(key, SESSION_TTL))
        .then();
  }

  @Override
  public Mono<PlaygroundSession> findById(String sessionId) {
    String key = buildKey(sessionId);

    return redisTemplate
        .opsForHash()
        .entries(key)
        .collectMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toString())
        .filter(map -> !map.isEmpty())
        .map(sessionMapper::fromMap)
        .switchIfEmpty(Mono.empty());
  }

  @Override
  public Mono<Void> delete(String sessionId) {
    String key = buildKey(sessionId);
    return redisTemplate.delete(key).then();
  }

  private String buildKey(String sessionId) {
    return REDIS_KEY_PREFIX + sessionId;
  }
}
