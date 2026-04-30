// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import java.util.Objects;

public record Term(int termDays) {
  public Term {
    Objects.requireNonNull(termDays);

    if (termDays <= 0) {
      throw new IllegalArgumentException("termDays must be greater than zero");
    }
  }
}
