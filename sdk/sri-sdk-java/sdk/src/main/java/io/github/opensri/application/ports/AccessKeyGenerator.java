// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application.ports;

import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.IssueDate;

/**
 * Generates the SRI access key for an electronic tax document.
 *
 * <p>This port defines the contract for producing the 49-digit access key required by the SRI to
 * identify a document before submission and authorization checks. Implementations are responsible
 * for combining the document metadata, environment, and verification digit according to SRI rules.
 */
public interface AccessKeyGenerator {

  /**
   * Produces the access key for the provided document context.
   *
   * <p>The generated value must be suitable for inclusion in the document XML and for later
   * authorization lookups against the SRI services.
   *
   * @param date issue date of the document being identified
   * @param documentNumber document numbering data, including code, establishment, emission point,
   *     and sequential number
   * @param taxInfo issuer tax information required by the SRI access key format
   * @param environment target SRI environment for which the key is generated
   * @return complete access key with verification digit
   */
  String generate(
      IssueDate date, DocumentNumber documentNumber, TaxInfo taxInfo, Environment environment);
}
