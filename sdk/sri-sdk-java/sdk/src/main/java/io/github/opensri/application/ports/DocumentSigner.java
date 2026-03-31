// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application.ports;

public interface DocumentSigner {

  String signDocument(String xmlDocument);
}
