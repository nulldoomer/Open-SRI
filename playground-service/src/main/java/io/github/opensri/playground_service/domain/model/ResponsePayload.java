// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.model;

import java.util.List;

public record ResponsePayload(String accessKey, String status, List<PayloadMessage> messages) {}
