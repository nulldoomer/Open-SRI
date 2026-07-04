// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.redis.utils;

import io.github.opensri.playground_service.domain.model.ResponsePayload;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class ResponsePayloadSerializer {
  private final ObjectMapper objectMapper;

  public ResponsePayloadSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String serialize(ResponsePayload payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JacksonException e) {
      throw new IllegalStateException("Cannot serialize response payload", e);
    }
  }

  public ResponsePayload deserialize(String json) {
    try {
      return objectMapper.readValue(json, ResponsePayload.class);
    } catch (JacksonException e) {
      throw new IllegalStateException("Cannot deserialize response payload", e);
    }
  }
}
