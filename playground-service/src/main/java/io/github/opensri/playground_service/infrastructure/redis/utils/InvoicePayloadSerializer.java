// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.redis.utils;

import io.github.opensri.playground_service.domain.model.InvoicePayload;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class InvoicePayloadSerializer {

  private final ObjectMapper objectMapper;

  public InvoicePayloadSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String serialize(InvoicePayload payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JacksonException e) {
      throw new IllegalStateException("Cannot serialize invoice payload", e);
    }
  }

  public InvoicePayload deserialize(String json) {
    try {
      return objectMapper.readValue(json, InvoicePayload.class);
    } catch (JacksonException e) {
      throw new IllegalStateException("Cannot deserialize invoice payload", e);
    }
  }
}
