// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.remissionguide.steps;

import io.github.opensri.domain.entities.remissionguide.TransportInfo;

/** Paso del constructor que define los datos del transportista y del transporte. */
public interface TransportInfoStep {
  /**
   * Define los datos del transportista y del transporte.
   *
   * @param transportInfo dirección de partida, transportista, período del transporte y placa
   * @return siguiente paso del constructor
   */
  RecipientsStep transportInfo(TransportInfo transportInfo);
}
