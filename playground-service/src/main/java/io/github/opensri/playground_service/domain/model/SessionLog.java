// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.model;

import java.time.Instant;

public record SessionLog(Instant timeStamp, String message) {}
