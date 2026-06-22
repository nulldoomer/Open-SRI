// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

/**
 * Request body for {@code POST /playground/run}.
 *
 * <p>Carries everything the SDK needs to emit one document: the target language, the SRI
 * environment, the signing certificate, the issuer profile and the invoice itself.
 *
 * <p>Security: the {@code .p12} bytes and passphrase are used in memory during the pipeline and are
 * never persisted or logged.
 *
 * @param lang SDK to run the pipeline with (currently {@code java})
 * @param environment SRI environment ({@code PRUEBAS} or {@code PRODUCCION})
 * @param certificate signing certificate material
 * @param issuer issuer tax data shared across the document
 * @param invoice the invoice to send
 */
public record PlaygroundRunRequest(
    String lang,
    String environment,
    Certificate certificate,
    Issuer issuer,
    InvoicePayload invoice) {

  /**
   * Signing certificate material.
   *
   * @param p12Base64 Base64-encoded PKCS#12 certificate bytes
   * @param passphrase certificate passphrase
   * @param alias private-key alias inside the certificate
   */
  public record Certificate(String p12Base64, String passphrase, String alias) {}

  /**
   * Issuer tax data used to build both the tax info and the issuer profile.
   *
   * @param socialReason issuer business name
   * @param ruc issuer RUC (13 digits)
   * @param parentAddress address of the issuer's head office
   * @param emissionType SRI emission type code (1 = normal)
   * @param accountingObligation {@code SI} or {@code NO}
   * @param specialTaxPayer special taxpayer registration number, or {@code null}
   */
  public record Issuer(
      String socialReason,
      String ruc,
      String parentAddress,
      int emissionType,
      String accountingObligation,
      String specialTaxPayer) {}
}
