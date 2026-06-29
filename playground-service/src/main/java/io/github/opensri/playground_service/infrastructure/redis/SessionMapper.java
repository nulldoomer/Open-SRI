// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.redis;

import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import io.github.opensri.playground_service.domain.model.SdkLanguage;
import io.github.opensri.playground_service.domain.model.SessionLog;
import io.github.opensri.playground_service.domain.model.SessionStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public Map<String, String> toMap(PlaygroundSession session) {
    Map<String, String> map = new HashMap<>();
    map.put(ID, session.id());
    map.put(LANGUAGE, session.language().name());
    map.put(SDK_VERSION, session.sdkVersion());
    map.put(STATUS, session.status().name());
    map.put(CREATED_AT, session.createdAt().toString());

    if (session.startedAt() != null) {
      map.put(STARTED_AT, session.startedAt().toString());
    }
    if (session.completedAt() != null) {
      map.put(COMPLETED_AT, session.completedAt().toString());
    }
    if (session.durationsMs() != null) {
      map.put(DURATION_MS, session.durationsMs().toString());
    }

    map.put(REQUEST_PAYLOAD, session.requestPayload());

    if (session.responsePayload() != null) {
      map.put(RESPONSE_PAYLOAD, session.responsePayload());
    }

    if (!session.logs().isEmpty()) {
      StringBuilder logsBuilder = new StringBuilder();
      for (int i = 0; i < session.logs().size(); i++) {
        SessionLog log = session.logs().get(i);
        if (i > 0) {
          logsBuilder.append("|||");
        }
        logsBuilder.append(log.timeStamp()).append("::").append(log.message());
      }
      map.put(LOGS, logsBuilder.toString());
    }

    if (session.errorMessage() != null) {
      map.put(ERROR_MESSAGE, session.errorMessage());
    }

    return map;
  }

  public PlaygroundSession fromMap(Map<String, String> map) {
    List<SessionLog> logs = parseLogs(map.get(LOGS));

    return new PlaygroundSession(
        map.get(ID),
        SdkLanguage.valueOf(map.get(LANGUAGE)),
        map.get(SDK_VERSION),
        SessionStatus.valueOf(map.get(STATUS)),
        Instant.parse(map.get(CREATED_AT)),
        map.get(STARTED_AT) != null ? Instant.parse(map.get(STARTED_AT)) : null,
        map.get(COMPLETED_AT) != null ? Instant.parse(map.get(COMPLETED_AT)) : null,
        map.get(DURATION_MS) != null ? Long.parseLong(map.get(DURATION_MS)) : null,
        map.get(REQUEST_PAYLOAD),
        map.get(RESPONSE_PAYLOAD),
        logs,
        map.get(ERROR_MESSAGE));
  }

  private List<SessionLog> parseLogs(String logsString) {
    List<SessionLog> logs = new ArrayList<>();
    if (logsString == null || logsString.isEmpty()) {
      return logs;
    }

    String[] logEntries = logsString.split("\\|\\|\\|");
    for (String entry : logEntries) {
      String[] parts = entry.split("::", 2);
      if (parts.length == 2) {
        logs.add(new SessionLog(Instant.parse(parts[0]), parts[1]));
      }
    }

    return logs;
  }
}
