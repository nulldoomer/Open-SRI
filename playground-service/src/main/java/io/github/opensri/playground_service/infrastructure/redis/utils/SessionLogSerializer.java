// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.redis.utils;

import io.github.opensri.playground_service.domain.model.SessionLog;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SessionLogSerializer {

  public static String serialize(List<SessionLog> logs) {

    if (logs == null || logs.isEmpty()) {
      return null;
    }

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < logs.size(); i++) {

      if (i > 0) {
        builder.append("|||");
      }

      builder.append(logs.get(i).timeStamp()).append("::").append(logs.get(i).message());
    }

    return builder.toString();
  }

  public static List<SessionLog> deserialize(String value) {

    List<SessionLog> logs = new ArrayList<>();

    if (value == null || value.isBlank()) {
      return logs;
    }

    String[] entries = value.split("\\|\\|\\|");

    for (String entry : entries) {

      String[] parts = entry.split("::", 2);

      if (parts.length == 2) {
        logs.add(new SessionLog(Instant.parse(parts[0]), parts[1]));
      }
    }

    return logs;
  }
}
