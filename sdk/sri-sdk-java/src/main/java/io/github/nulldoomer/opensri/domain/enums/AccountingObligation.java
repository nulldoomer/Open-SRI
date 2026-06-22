// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.enums;

/**
 * Representa la obligación contable declarada por el emisor del comprobante.
 *
 * <p>Este valor se utiliza para indicar en el documento electrónico si el contribuyente está
 * obligado o no a llevar contabilidad, conforme al dato que debe reflejarse en la información
 * fiscal del comprobante.
 */
public enum AccountingObligation {
  /** Indica que el emisor está obligado a llevar contabilidad. */
  SI,
  /** Indica que el emisor no está obligado a llevar contabilidad. */
  NO
}
