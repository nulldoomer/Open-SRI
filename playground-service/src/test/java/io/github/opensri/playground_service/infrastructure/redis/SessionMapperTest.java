// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.redis;

import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import io.github.opensri.playground_service.domain.model.SessionLog;
import io.github.opensri.playground_service.domain.model.SessionStatus;
import io.github.opensri.playground_service.domain.model.SdkLanguage;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SessionMapperTest {

  private final SessionMapper mapper = new SessionMapper();

  @Test
  public void map_to_redis_and_back_preserves_session() {
    PlaygroundSession session = new PlaygroundSession(
        "session-123",
        SdkLanguage.JAVA,
        "1.2.4",
        SessionStatus.COMPLETED,
        Instant.parse("2026-06-28T19:00:00Z"),
        Instant.parse("2026-06-28T19:01:00Z"),
        Instant.parse("2026-06-28T19:02:00Z"),
        5000L,
        "{\"invoice\": \"data\"}",
        "<response>OK</response>",
        List.of(
            new SessionLog(Instant.parse("2026-06-28T19:01:00Z"), "Starting execution"),
            new SessionLog(Instant.parse("2026-06-28T19:02:00Z"), "Done")),
        null);

    var map = mapper.toMap(session);
    var recovered = mapper.fromMap(map);

    assert recovered.id().equals(session.id());
    assert recovered.language() == session.language();
    assert recovered.status() == session.status();
    assert recovered.logs().size() == 2;
  }
}
