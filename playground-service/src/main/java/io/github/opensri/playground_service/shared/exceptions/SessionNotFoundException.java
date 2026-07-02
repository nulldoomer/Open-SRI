// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.shared.exceptions;

public class SessionNotFoundException extends RuntimeException {
  public SessionNotFoundException(String id) {
    super("Session not found: " + id);
  }
}
