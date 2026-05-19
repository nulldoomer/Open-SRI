// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.shared.exceptions.OpenSRIValidationException;

public record Term(int termDays) {
  public Term {
    if (termDays <= 0) {
      throw new OpenSRIValidationException("termDays must be greater than zero");
    }
  }
}
