// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.redis;

import io.github.opensri.playground_service.domain.model.*;
import io.github.opensri.playground_service.infrastructure.redis.utils.InvoicePayloadSerializer;
import io.github.opensri.playground_service.infrastructure.redis.utils.ResponsePayloadSerializer;
import io.github.opensri.playground_service.infrastructure.redis.utils.SessionLogSerializer;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

  private static final String ID = "id";
  private static final String LANGUAGE = "language";
  private static final String SDK_VERSION = "sdkVersion";
  private static final String STATUS = "status";
  private static final String CREATED_AT = "createdAt";
  private static final String STARTED_AT = "startedAt";
  private static final String COMPLETED_AT = "completedAt";
  private static final String DURATION_MS = "durationMs";
  private static final String REQUEST_PAYLOAD = "requestPayload";
  private static final String RESPONSE_PAYLOAD = "responsePayload";
  private static final String LOGS = "logs";
  private static final String ERROR_MESSAGE = "errorMessage";

  private final InvoicePayloadSerializer payloadSerializer;
  private final ResponsePayloadSerializer responsePayloadSerializer;

  public SessionMapper(InvoicePayloadSerializer payloadSerializer, ResponsePayloadSerializer responsePayloadSerializer) {
    this.payloadSerializer = payloadSerializer;
      this.responsePayloadSerializer = responsePayloadSerializer;
  }

  public Map<String, Object> toMap(PlaygroundSession session) {

    // Map object to store data key - value data on Redis
    Map<String, Object> map = new HashMap<>();
    map.put(ID, session.id());
    map.put(LANGUAGE, session.language().name());
    map.put(SDK_VERSION, session.sdkVersion());
    map.put(STATUS, session.status().name());
    map.put(CREATED_AT, session.createdAt().toString());
    map.put(REQUEST_PAYLOAD, payloadSerializer.serialize(session.requestPayload()));

    putIfPresent(map, STARTED_AT, session.startedAt(), Instant::toString);
    putIfPresent(map, COMPLETED_AT, session.completedAt(), Instant::toString);
    putIfPresent(map, DURATION_MS, session.durationsMs(), Object::toString);
    putIfPresent(map, RESPONSE_PAYLOAD, responsePayloadSerializer.serialize(session.responsePayload()),
            Function.identity());
    putIfPresent(map, ERROR_MESSAGE, session.errorMessage(), Function.identity());

    if (!session.logs().isEmpty()) {
      map.put(LOGS, SessionLogSerializer.serialize(session.logs()));
    }
    return map;
  }

  // Generic method to handle null validation
  private <T> void putIfPresent(
      Map<String, Object> map, String key, T value, Function<T, String> toString) {

    if (value != null) {
      map.put(key, toString.apply(value));
    }
  }

  public PlaygroundSession fromMap(Map<String, String> map) {

    InvoicePayload payload = payloadSerializer.deserialize(map.get(REQUEST_PAYLOAD));
    ResponsePayload responsePayload = responsePayloadSerializer.deserialize(map.get(RESPONSE_PAYLOAD));
    List<SessionLog> logs = SessionLogSerializer.deserialize(map.get(LOGS));

    return new PlaygroundSession(
        map.get(ID),
        SdkLanguage.valueOf(map.get(LANGUAGE)),
        map.get(SDK_VERSION),
        SessionStatus.valueOf(map.get(STATUS)),
        Instant.parse(map.get(CREATED_AT)),
        map.get(STARTED_AT) != null ? Instant.parse(map.get(STARTED_AT)) : null,
        map.get(COMPLETED_AT) != null ? Instant.parse(map.get(COMPLETED_AT)) : null,
        map.get(DURATION_MS) != null ? Long.parseLong(map.get(DURATION_MS)) : null,
        payload,
        responsePayload,
        logs,
        map.get(ERROR_MESSAGE));
  }
}
