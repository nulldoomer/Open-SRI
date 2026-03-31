// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.domain.enums.IdentificationType;

/**
 * Represents the special "Final Consumer" identification.
 *
 * <p>This identification is a fixed value defined by the tax authority and does not vary.
 *
 * <p>Instances of this class always represent the constant final consumer identification number.
 *
 * <p>A singleton is used because this value object has exactly one valid state.
 */
public final class FinalConsumer implements ClientIdentification {
  private static final FinalConsumer INSTANCE = new FinalConsumer();
  private static final String VALUE = "9999999999999";

  private FinalConsumer() {}

  /**
   * Returns the shared instance representing the SRI final consumer identification.
   *
   * @return singleton instance of {@code FinalConsumer}
   */
  public static FinalConsumer instance() {
    return INSTANCE;
  }

  /**
   * Returns the SRI identification type for the final consumer category.
   *
   * @return {@link IdentificationType#VENTA_CONSUMIDOR_FINAL}
   */
  @Override
  public IdentificationType identificationType() {
    return IdentificationType.VENTA_CONSUMIDOR_FINAL;
  }

  /**
   * Returns the fixed identification value mandated for final consumer sales.
   *
   * @return constant final consumer identification number
   */
  @Override
  public String value() {
    return VALUE;
  }
}
